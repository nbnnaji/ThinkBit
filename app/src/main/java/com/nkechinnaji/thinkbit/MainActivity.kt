package com.nkechinnaji.thinkbit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.ParseException
import coil.compose.rememberAsyncImagePainter
import com.nkechinnaji.thinkbit.news.model.Articles
import com.nkechinnaji.thinkbit.news.model.Source
import com.nkechinnaji.thinkbit.news.model.uimodel.ArticleUiModel
import com.nkechinnaji.thinkbit.news.model.uimodel.toUiModel
import com.nkechinnaji.thinkbit.ui.theme.ThinkBitTheme
import com.nkechinnaji.thinkbit.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        viewModel.everyNewsObserver.observe(this) { news ->
            setContent {
                ThinkBitTheme {
                    getNewsList(news)

                }
            }
        }
        viewModel.getEveryNews()
    }


//With Search & Load More
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun getNewsList(articleItems: ArrayList<Articles>) {
        var searchQuery by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(false) }

        // State for "Show More" functionality for the main list
        var visibleMainItemCount by remember { mutableIntStateOf(5) }

        // Convert raw articles to UI models once
        val allArticleUiModels = remember(articleItems) {
            articleItems.toUiModel()
        }

        // This list is used for the search suggestions when the search bar is active
        val filteredArticlesForSearchSuggestions = remember(searchQuery, active, allArticleUiModels) {
            if (searchQuery.isBlank()) {
                if (active) { // Show nothing or recent searches if search is active and query blank
                    emptyList()
                } else { // Should not happen if active is false and query blank (covered by main list)
                    allArticleUiModels
                }
            } else {
                allArticleUiModels.filter { article ->
                    val query = searchQuery.trim()
                    (article.title?.contains(query, ignoreCase = true) == true) ||
                            (article.desc?.contains(query, ignoreCase = true) == true) ||
                            (article.author?.contains(query, ignoreCase = true) == true)
                }
            }
        }

        // This is the list for the main screen content (outside search bar's active state)
        // It will now also respect the visibleMainItemCount
        val mainScreenDisplayList = remember(searchQuery, active, allArticleUiModels, visibleMainItemCount) {
            val baseList = if (!active) { // Only apply "show more" when search is NOT active
                if (searchQuery.isBlank()) {
                    allArticleUiModels
                } else {
                    // If search is not active but query is present (e.g. after a search),
                    // show filtered results without "show more" for simplicity,
                    // or decide if "show more" should apply here too.
                    // For now, showing all filtered results.
                    allArticleUiModels.filter { article -> // Re-filter based on searchQuery
                        val query = searchQuery.trim()
                        (article.title?.contains(query, ignoreCase = true) == true) ||
                                (article.desc?.contains(query, ignoreCase = true) == true) ||
                                (article.author?.contains(query, ignoreCase = true) == true)
                    }
                }
            } else { // Search is active, main list area is typically empty or shows different content
                emptyList()
            }

            if (!active && searchQuery.isBlank()) { // Apply "take" only for the initial, unfiltered list
                baseList.take(visibleMainItemCount)
            } else {
                baseList // For search results or when search is active, show all derived items
            }
        }


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    Text(
                        text = "News Headlines",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 55.dp, end = 16.dp),
                        textAlign = TextAlign.Center
                    )

                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            // Potentially reset visibleMainItemCount if search interaction should affect main list pagination
                            // if (active && it.isBlank()) {
                            // visibleMainItemCount = 5
                            // }
                        },
                        onSearch = {
                            active = false
                            // When a search is executed, we typically show all results from that search.
                            // The "show more" on the main list might not be relevant here.
                        },
                        active = active,
                        onActiveChange = { isActive ->
                            active = isActive
                            if (!isActive && searchQuery.isBlank()) {
                                // If search becomes inactive and query is blank, ensure main list pagination is fresh
                                // visibleMainItemCount = 5 // Or maintain current state if preferred
                                Log.d("SearchState", "Search dismissed. Showing main list.")
                            }
                        },
                        placeholder = { Text("Search articles...") },
                        leadingIcon = { Icon(Icons.Filled.Search, "Search Icon") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        searchQuery = ""
                                        // active = false // Optionally deactivate search on clear
                                        // visibleMainItemCount = 5 // Reset main list if clearing search returns to it
                                    },
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        // Content for when SearchBar is active (showing suggestions)
                        if (searchQuery.isBlank() && filteredArticlesForSearchSuggestions.isEmpty() && active) {
                            Text("Type to search for news articles", modifier = Modifier.padding(16.dp))
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                if (filteredArticlesForSearchSuggestions.isEmpty() && searchQuery.isNotBlank()) {
                                    item {
                                        Text(
                                            "No results found for \"$searchQuery\"",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                } else {
                                    items(
                                        items = filteredArticlesForSearchSuggestions, // Show all suggestions
                                        key = { article -> article.url }
                                    ) { model ->
                                        NewsCardView(
                                            modifier = Modifier
                                                .fillParentMaxWidth()
                                                .padding(vertical = 4.dp),
                                            article = model
                                        ) {
                                            active = false // Dismiss search bar on item click
                                            searchQuery = model.title ?: "" // Populate search with title
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            // Main content area (visible when search is not active, or after search)
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f), // Allow LazyColumn to take available space
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(
                        items = mainScreenDisplayList, // Use the paginated list
                        key = { article -> article.url }
                    ) { articleUiModelData ->
                        NewsCardView(
                            modifier = Modifier.fillParentMaxWidth(),
                            article = articleUiModelData
                        ) {
                            // Handle item click on main list
                            Log.d("NewsClick", "Clicked on: ${articleUiModelData.title}")
                        }
                    }
                }

                // "Show More" button for the main list, only if search is not active and query is blank
                if (!active && searchQuery.isBlank() && visibleMainItemCount < allArticleUiModels.size) {
                    Button(
                        onClick = {
                            visibleMainItemCount += 5
                            if (visibleMainItemCount > allArticleUiModels.size) {
                                visibleMainItemCount = allArticleUiModels.size
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Show More (${allArticleUiModels.size - visibleMainItemCount} remaining)")
                    }
                } else if (!active && searchQuery.isBlank() && allArticleUiModels.isNotEmpty() && visibleMainItemCount >= allArticleUiModels.size) {
                    Text(
                        "All articles loaded.",
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }


    @Composable
    fun NewsCardView(modifier: Modifier = Modifier, article: ArticleUiModel, onCLick: () -> Unit) {
        Card(
            modifier = modifier
                .clickable(onClick = onCLick)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    // use surface to give a background color
                    // as loading placeholder
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        // set the shape automatically clip the image inside it
                        shape =  CircleShape
                    ) {

                        var newsImage = rememberAsyncImagePainter(article.imageUrl)
                        Image(
                            painter = newsImage,
                            contentDescription = article.title, // Provide meaningful content description
                            modifier = Modifier
                                .size(10.dp)
                                .padding(end = 16.dp),
                            contentScale = ContentScale.Crop
                        )

                    }
                    // the publisher name
                    Text(
                        text = if (article.author.isBlank()) "" else {"  Author: ${article.author}"},
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
                // the news date
                Text(
                    text = "Date: ${formatPublicationDateSDF(article.publication)}",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
                    .height(164.dp),

                shape = RoundedCornerShape(12.dp)
            ) {
                // the news image
                rememberAsyncImagePainter(article.imageUrl)

                var newsImage = rememberAsyncImagePainter(article.imageUrl)
                Image(
                    painter = newsImage,
                    contentDescription = article.title,
                    modifier = Modifier
                        .size(10.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // news title
            Text(
                text = article.title ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            // news short description
            Text(
                text = article.desc ?: "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
            )
            // horizontal line on the bottom side of the news card
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
        }
    }


    // --- Preview with Sample Data ---
    @Preview(showBackground = true)
    @Composable
    fun ArticleListPreview() {
        val sampleArticles = arrayListOf(
            Articles(
                source = Source(id = "", name = "The Verge"),
                author = "Justine Calma",
                title = "Breaking News: Compose is Awesome!",
                description = "Jetpack Compose simplifies Android UI development.",
                url = "https://www.theverge.com/news/685820/google-ai-forecast-typhoon-hurricane-tropical-storm",
                urlToImage = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/06/Cyclone-header-image.png?quality=90&strip=all&crop=0%2C3.4613147178592%2C100%2C93.077370564282&w=1200",
                publishedAt = "July 10, 2025"
            ),
            Articles(
                source = Source(id = "", name = "Wired"),
                author = "Hilary Beaumont",
                title = "The Viral Storm Streamers Predicting Deadly Tornadoes—Sometimes Faster Than the Government",
                description = "Jetpack Compose simplifies Android UI development.",
                url = "https://www.theverge.com/news/685820/google-ai-forecast-typhoon-hurricane-tropical-storm",
                urlToImage = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/06/Cyclone-header-image.png?quality=90&strip=all&crop=0%2C3.4613147178592%2C100%2C93.077370564282&w=1200",
                publishedAt = "July 10, 2025"
            ),

            Articles(
                source = Source(id = "", name = "Wired"),
                author = "Hilary Beaumont",
                title = "The Viral Storm Streamers Predicting Deadly Tornadoes—Sometimes Faster Than the Government",
                description = "Jetpack Compose simplifies Android UI development.",
                url = "https://www.theverge.com/news/685820/google-ai-forecast-typhoon-hurricane-tropical-storm",
                urlToImage = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/06/Cyclone-header-image.png?quality=90&strip=all&crop=0%2C3.4613147178592%2C100%2C93.077370564282&w=1200",
                publishedAt = "July 10, 2025"
            ),
            Articles(
                source = Source(id = "", name = "Wired"),
                author = "Hilary Beaumont",
                title = "The Viral Storm Streamers Predicting Deadly Tornadoes—Sometimes Faster Than the Government",
                description = "Jetpack Compose simplifies Android UI development.",
                url = "https://www.theverge.com/news/685820/google-ai-forecast-typhoon-hurricane-tropical-storm",
                urlToImage = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/06/Cyclone-header-image.png?quality=90&strip=all&crop=0%2C3.4613147178592%2C100%2C93.077370564282&w=1200",
                publishedAt = "July 10, 2025"
            )
        )
        ThinkBitTheme {
            getNewsList(articleItems = sampleArticles)
        }
    }

    fun formatPublicationDateSDF(dateString: String?): String {
        if (dateString.isNullOrBlank()) {
            return "N/A"
        }

        // Define potential input patterns for SimpleDateFormat
        // Note: Order matters. More specific patterns should ideally come first.
        val inputPatterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",     // Example: "2023-10-26T10:15:30Z" (ISO 8601 UTC)
            "yyyy-MM-dd'T'HH:mm:ssXXX",    // Example: "2023-10-26T10:15:30+01:00" (ISO 8601 with offset)
            "MMMM d, yyyy",                // Example: "July 10, 2025"
            "yyyy-MM-dd"                   // Example: "2023-10-26"
            // Add more patterns here if your date strings can have other formats
        )

        var parsedDate: Date? = null

        for (pattern in inputPatterns) {
            try {
                val inputFormat = SimpleDateFormat(
                    pattern,
                    Locale.ENGLISH
                ) // Use Locale.ENGLISH if month names are in English
                // For SimpleDateFormat, being lenient can sometimes help, but also be risky.
                // inputFormat.isLenient = false // Consider making it non-lenient for stricter parsing
                parsedDate = inputFormat.parse(dateString)
                if (parsedDate != null) {
                    break // Successfully parsed
                }
            } catch (e: ParseException) {
                // Try the next pattern
            }
        }

        return if (parsedDate != null) {
            // Define the formatter for the desired output ("MM/dd/yyyy")
            val outputFormat = SimpleDateFormat(
                "MM/dd/yyyy",
                Locale.US
            ) // Using Locale.US for consistency in output
            outputFormat.format(parsedDate)
        } else {
            Log.e(
                "DateParseErrorSDF",
                "Could not parse date: $dateString with any known SDF pattern."
            )
            dateString // Return original if parsing fails
        }
    }

}


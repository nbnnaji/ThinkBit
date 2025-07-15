package com.nkechinnaji.thinkbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nkechinnaji.thinkbit.news.model.Articles
import com.nkechinnaji.thinkbit.news.model.Source
import com.nkechinnaji.thinkbit.news.model.uimodel.ArticleUiModel
import com.nkechinnaji.thinkbit.news.model.uimodel.toUiModel
import com.nkechinnaji.thinkbit.ui.theme.ThinkBitTheme
import com.nkechinnaji.thinkbit.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: NewsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.everyNewsObserver.observe(this){ news ->
            setContent {
                ThinkBitTheme {
                    MyListScreen(news)
                }
            }
        }
        viewModel.getEveryNews()

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListScreen(articleItems: ArrayList<Articles>) {
    val articleUiModel = remember(articleItems){
        articleItems.toUiModel()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("News") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding) // Apply padding from Scaffold
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp) // Padding for the content inside LazyColumn
        ) {

            items(
                items = articleUiModel
                //, key = {uiModel -> uiModel.source }
            ) { articleUiModel ->
                // Call a Composable function to render this specific article
               // ListItemRow(item = articleUiModel)
                NewsCardView(
                    modifier = Modifier.fillParentMaxWidth(),
                    article = articleUiModel) {

                }
            }
        }
    }
}


@Composable
fun NewsCardView(modifier: Modifier = Modifier, article: ArticleUiModel, onCLick: () -> Unit){
    Card(
        modifier = modifier
            .clickable(onClick = onCLick)
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)){
                // use surface to give a background color
                // as loading placeholder
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    // set the shape automatically clip the image inside it
                    shape = CircleShape
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
                    text = " – ${article.author}",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
            // the news date
            Text(
                text = article.publication?: "",
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
            text = article.title?: "",
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
            source = Source(id ="",name = "The Verge"),
            author = "Justine Calma",
            title = "Breaking News: Compose is Awesome!",
            description = "Jetpack Compose simplifies Android UI development.",
            url = "https://www.theverge.com/news/685820/google-ai-forecast-typhoon-hurricane-tropical-storm",
            urlToImage = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/06/Cyclone-header-image.png?quality=90&strip=all&crop=0%2C3.4613147178592%2C100%2C93.077370564282&w=1200",
            publishedAt = "July 10, 2025"
        ),
        Articles(
            source = Source(id ="",name = "Wired"),
            author = "Hilary Beaumont",
            title = "The Viral Storm Streamers Predicting Deadly Tornadoes—Sometimes Faster Than the Government",
            description = "Jetpack Compose simplifies Android UI development.",
            url = "https://www.theverge.com/news/685820/google-ai-forecast-typhoon-hurricane-tropical-storm",
            urlToImage = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/06/Cyclone-header-image.png?quality=90&strip=all&crop=0%2C3.4613147178592%2C100%2C93.077370564282&w=1200",
            publishedAt = "July 10, 2025"
        ),

        Articles(
            source = Source(id ="",name = "Wired"),
            author = "Hilary Beaumont",
            title = "The Viral Storm Streamers Predicting Deadly Tornadoes—Sometimes Faster Than the Government",
            description = "Jetpack Compose simplifies Android UI development.",
            url = "https://www.theverge.com/news/685820/google-ai-forecast-typhoon-hurricane-tropical-storm",
            urlToImage = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/06/Cyclone-header-image.png?quality=90&strip=all&crop=0%2C3.4613147178592%2C100%2C93.077370564282&w=1200",
            publishedAt = "July 10, 2025"
        ),
        Articles(
            source = Source(id ="",name = "Wired"),
            author = "Hilary Beaumont",
            title = "The Viral Storm Streamers Predicting Deadly Tornadoes—Sometimes Faster Than the Government",
            description = "Jetpack Compose simplifies Android UI development.",
            url = "https://www.theverge.com/news/685820/google-ai-forecast-typhoon-hurricane-tropical-storm",
            urlToImage = "https://platform.theverge.com/wp-content/uploads/sites/2/2025/06/Cyclone-header-image.png?quality=90&strip=all&crop=0%2C3.4613147178592%2C100%2C93.077370564282&w=1200",
            publishedAt = "July 10, 2025"
        )
    )
    ThinkBitTheme {
        MyListScreen(articleItems = sampleArticles)
    }
}


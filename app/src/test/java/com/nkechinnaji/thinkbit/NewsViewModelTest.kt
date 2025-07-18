package com.nkechinnaji.thinkbit

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nkechinnaji.thinkbit.news.model.Articles
import com.nkechinnaji.thinkbit.news.model.Source
import com.nkechinnaji.thinkbit.news.service.NewsService
import com.nkechinnaji.thinkbit.news.viewmodel.NewsViewModel
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Before
import org.mockito.Mock
import kotlinx.coroutines.test.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NewsViewModelTest {

    // For LiveData testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Mocks service layer & matches viewmodel's dependency
     */
    @Mock
    private lateinit var mockNewsService: NewsService

    /**
     * Provides more control over coroutine execution in tests
     */
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: NewsViewModel

    /***
     * Replace the main dispatcher for viewModelScope
     * Instantiate the ViewModel with the mock NewsService
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        viewModel = NewsViewModel(mockNewsService) // Matches NewsViewModel's constructor
    }

    /**
     * For resetting Dispatchers.Main
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getEveryNews updates news observer on successful fetch`() = runTest(testDispatcher) {
        // mock data
        val mockArticles = arrayListOf(
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

        val mockServiceResponse = com.nkechinnaji.thinkbit.news.model.ArticlesResponse(
            status = "ok",
            totalResults = mockArticles.size,
            articles = mockArticles
        )
        whenever(mockNewsService.getEveryNewsService()).thenReturn(mockServiceResponse)
        val observer = Mockito.mock(Observer::class.java) as Observer<List<Articles>>
        viewModel.everyNewsLd.observeForever(observer)

        // Act
        viewModel.getEveryNews()
        advanceUntilIdle()
        val actualArticles = viewModel.everyNewsLd.value

        verify(observer).onChanged(mockArticles)
        com.google.common.truth.Truth.assertThat(actualArticles).isEqualTo(mockArticles)
        com.google.common.truth.Truth.assertThat(viewModel.errorLd.value).isNull()
    }


    @Test
    fun `getEveryNews updates error observer on service exception`() = runTest(testDispatcher) {

        val errorMessage = "Network Error"
        whenever(mockNewsService.getEveryNewsService()).thenThrow(RuntimeException(errorMessage))
        val observer = Mockito.mock(Observer::class.java) as Observer<Boolean>

        // Act
        viewModel.getEveryNews()
        advanceUntilIdle()

        // Assert
        com.google.common.truth.Truth.assertThat(viewModel.everyNewsLd.value).isEmpty()
        com.google.common.truth.Truth.assertThat(viewModel.errorLd.value)
            .contains(errorMessage)
    }

    @Test
    fun `getEveryNews handles null response from service`() = runTest(testDispatcher) {

        whenever(mockNewsService.getEveryNewsService()).thenReturn(null)

        // Act
        viewModel.getEveryNews()
        advanceUntilIdle()

        // Assert
        com.google.common.truth.Truth.assertThat(viewModel.everyNewsLd.value?.isEmpty())
            .isTrue()
        com.google.common.truth.Truth.assertThat(viewModel.errorLd.value)
            .isEqualTo("Failed to retrieve news. Please try again later.")
    }

    @Test
    fun `getEveryNews handles empty articles list from service`() = runTest(testDispatcher) {
        val mockServiceResponse = com.nkechinnaji.thinkbit.news.model.ArticlesResponse(
            status = "ok",
            totalResults = 0,
            articles = ArrayList() // Empty list
        )
        whenever(mockNewsService.getEveryNewsService()).thenReturn(mockServiceResponse)

        // Act
        viewModel.getEveryNews()
        advanceUntilIdle()

        // Assert
        com.google.common.truth.Truth.assertThat(viewModel.everyNewsLd.value?.isEmpty())
            .isTrue()

    }
}

import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

//операции ввода-вывода будут выполняться в фоновом потоке, приостанавливаемая функция
suspend fun isWebsiteAvailable(url: String): Boolean = withContext(Dispatchers.IO) {
    try {
        val connection = URL(url).openConnection() as HttpURLConnection //установка соединения, приведение к типу
        connection.requestMethod = "HEAD" //получить только заголовки ответа без тела ответа
        connection.connectTimeout = 5000 //время ожидания
        connection.readTimeout = 5000 //время чтения
        connection.responseCode == HttpURLConnection.HTTP_OK //проверка на успешный ответ
    } catch (e: Exception) {
        false
    }
}

fun main() = runBlocking {
    val websites = listOf("https://www.google.com",
        "https://www.facebook.com",
        "https://www.github.com",
        "https://www.twitter.com",
        "https://www.instagram.com") //список

    val results = websites.map { url ->
        async { url to isWebsiteAvailable(url) }
    }.awaitAll()
    /*
        коллекция пар<url, результат проверки>
        async запускает асинхронную операцию для каждого URL-адреса
        awaitAll() - ждет завершения всех запущенных асинхронных операций, возвращает список результатов
     */

    for ((url, available) in results) {
        if (available) println("Сайт $url доступен")
        else println("Сайт $url не доступен")
    }
}
import com.github.kittinunf.fuel.httpGet
import com.github.magneticflux.rss.createRssPersister
import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val urls = listOf(
        "https://www.tagesschau.de/xml/rss2_https",
        "https://www.deutschlandfunk.de/die-nachrichten.353.de.rss",
        "https://www.swr.de/~rss/swraktuell/swraktuell-bw-100.xml",
        "http://apod.nasa.gov/apod.rss",
        "https://xkcd.com/rss.xml",
        "http://planet.debian.org/rss20.xml",
    )
    val persister = createRssPersister()

    File("index.html").writeText(createHTML().html {
        head { title { +"KRSSC" } }
        body {
            h1 {
                +"RSS Feed ${Date()}"
            }
            for (url in urls) {
                val (request, response, result) = url.httpGet().responseString(Charsets.UTF_8)
                val rssFeed = persister.read(Rss::class.java, result.get())

                +"${rssFeed.channel.title}"

                for (item in rssFeed.channel.items.sortedByDescending { it.pubDate }.take(10)) {
                    ul {
                        item.title?.let { title ->
                            li {
                                a(href = item.link?.toString()) {
                                    +"$title (${item.pubDate})"
                                }
                            }
                        }
                    }
                }

            }
        }
    }, Charsets.UTF_8)
}
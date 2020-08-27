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
                h2 {
                    +rssFeed.channel.title
                }
                for (item in rssFeed.channel.items) {
                    item.title?.let {
                        p {
                            a(href = item.link?.toString()) {
                                +it
                            }
                        }
                    }
                }
            }
        }
    }, Charsets.UTF_8)
}
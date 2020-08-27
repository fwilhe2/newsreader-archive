import kotlinx.html.stream.createHTML
import com.github.kittinunf.fuel.httpGet
import com.github.magneticflux.rss.createRssPersister
import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import kotlinx.html.*
import java.io.File

fun main(args: Array<String>) {
    val urls = listOf(
        "https://www.tagesschau.de/xml/rss2_https",
        "http://apod.nasa.gov/apod.rss",
        "http://planet.debian.org/rss20.xml",
    )
    val persister = createRssPersister()

    File("index.html").writeText(createHTML().html {
        head { title { +"KRSSC" } }
        body {
            for (url in urls) {
                val (request, response, result) = url.httpGet().responseString()
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
    })
}
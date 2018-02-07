import org.gradle.api.DefaultTask
import java.util.Random

@CacheableTask
open class TestTask : DefaultTask() {

    @Input
    var input: String = ""

    @OutputFile
    val output = File("build/outputfile.txt")

    @TaskAction
    fun action() {
        output.writeText(input)
    }

}

task("tester", TestTask::class) {
    input = properties["prop"] as String? ?: Random().nextInt(2000).toString()
}

task("clean") {
    File("build/outputfile.txt").delete()
}

tasks["tester"].dependsOn("clean")
package fr.not_here.not_tower_defense.config

import fr.not_here.not_tower_defense.NotTowerDefense.Companion.instance
import fr.not_here.not_tower_defense.config.containers.*
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.introspector.Property
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.nodes.NodeTuple
import org.yaml.snakeyaml.nodes.Tag
import java.io.*
import java.lang.Exception
import java.nio.charset.StandardCharsets

object ConfigLoader {
    @JvmStatic
    fun getFile(configFile: String): File {
        var file = File(instance.dataFolder, "$configFile.yml")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            instance.saveResource("$configFile.yml", false)
            file = File(instance.dataFolder, "$configFile.yml")
        }
        return file
    }

    fun <T> getYaml(tClass: Class<T>?): Yaml {
        return Yaml(
            CustomClassLoaderConstructor(ConfigLoader::class.java.classLoader, LoaderOptions()),
            representer.apply { addClassTag(tClass, Tag.MAP) },
            DumperOptions().apply {
                splitLines = false
                indent = 2
                isPrettyFlow = true
                defaultFlowStyle = DumperOptions.FlowStyle.BLOCK // Fix below - additional configuration
            }
        )
    }

    inline fun <reified T> loadConfig(configFile: String): T? {
        return try {
            getYaml(T::class.java).loadAs(FileInputStream(getFile(configFile)), T::class.java)
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    inline fun <reified T> saveConfig(configFile: String, obj: T) {
        try {
            getYaml(T::class.java).dump(obj, getFile(configFile).bufferedWriter(StandardCharsets.UTF_8))
        } catch (e: Exception) { e.printStackTrace() }
    }

    private val representer: Representer
        get() {
            val representer: Representer = object : Representer(DumperOptions()) {
                override fun representJavaBeanProperty(javaBean: Any?, property: Property?, propertyValue: Any?, customTag: Tag?): NodeTuple? {
                    when (propertyValue){
                        null -> return null
                        is Map<*,*> -> if(propertyValue.size == 0) return null
                        is Set<*> -> if(propertyValue.size == 0) return null
                        is List<*> -> if(propertyValue.size == 0) return null
                        is Array<*> -> if(propertyValue.size == 0) return null
                        is String -> if(propertyValue == "") return null
                    }
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag)
                }
            }
            representer.propertyUtils.isSkipMissingProperties = true
            return representer
        }

    fun init() {
        MobsConfigContainer.load()
        TowersConfigContainer.load()
        PowersConfigContainer.load()
        GamesConfigContainer.load()
        GlobalConfigContainer.load()
    }
}
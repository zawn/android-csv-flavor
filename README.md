
 [ ![Download](https://api.bintray.com/packages/zhangzhenli/maven/android-csv-variants/images/download.svg) ](https://bintray.com/zhangzhenli/maven/android-csv-variants/_latestVersion)
 
## 目的
用于在Android Gradle构建时通过CSV文件配置Variants.简单的说就是通过CSV文件配置渠道包,或者Android Studio 分渠道打包.
 

## 用法
 1. 添加CSV文件.
    
    在项目的根目录下添加variant.csv文件.格式要求:

    第一行:标题.(用于人阅读,不参与打包过程.)
    
    第二行:最终buildVariants(参见步骤3)中的key.
    
    第三行以后:最终buildVariants中的value.
    >参见源码目录下的variant.csv文件.
 2. 引入依赖.

    在buildscript中添加依赖:
    ``` groovy
    classpath 'com.house365.build:tools:1.0.3'
    ```
    最终效果可能如下格式:
    ``` groovy
     buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:1.3.1'
            classpath 'com.house365.build:tools:1.0.3'
    
            // NOTE: Do not place your application dependencies here; they belong
            // in the individual module build.gradle files
        }
    }
    ```

 3. 在build.gradle中添加配置.

    ``` groovy
    android {
        LinkedHashMap<String, LinkedHashMap<String, String>> buildVariants = VariantsUtil.readVariantsFromFile(project)
    
        /**
         * 添加Flavor
         */
        productFlavors {
            buildVariants.each { flavorName, keyValueMap ->
                "$flavorName" {
                    applicationId = keyValueMap.get("applicationId")
    //            resValue "string", "app_name2", "Some new value"
    //            buildConfigField "boolean", 'analyse_flag', channel.get("analyse_flag").toLowerCase()
    //            manifestPlaceholders = [UMENG_CHANNEL_VALUE: channel.get("name")]
                }
            }
        }
    
        /**
         * 过滤掉配置文件中未指定的衍生版本.
         * 即去掉配置文件中未指定的Flavor + BuildType组合.
         */
        variantFilter { filter ->
            filter.flavors.each { flavor ->
                if (buildVariants.containsKey(flavor.name)) {
                    final LinkedHashMap<String, String> keyValueMap = buildVariants.get(flavor.name)
                    def buildTypeArray = keyValueMap.get("buildType").replace("；", ";").split(";")
                    if (filter.buildType.name in buildTypeArray) {
                        filter.ignore = false
                        println "Activate variant :" + String.format("%15s %s", flavor.name, filter.buildType.name)
                    } else {
                        filter.ignore = true
                    }
                }
            }
        }
    }
    ```
    
## 其他说明
#### 配置文件指定
默认的`VariantsUtil`类将首先读取`project`的`variantFileURL`属性,该属性可以通过文件gradle.properties指定,也可以通过命令行传入,具体格式如下:
 - gradle.properties指定:

    `variantFileURL=%variantFileURLValue%`
 - 命令行传入:

    `-PvariantFileURL=%variantFileURLValue%`
    
其中`variantFileURLValue`支持相对路径(相对于project目录)/绝对路径/URL.
如果项目没有指定`variantFileURL`属性,则默认读取project/variant.csv文件.

#### flavorName
 1. 程序将从CSV文件中寻找flavorName列,如果没有flavorName列则寻找name列,并复制name列的值作为flavorName列.如果name列任然没有找到,则复制CSV的第一列的值作为flavorName列.
    
 2. 检查flavorName列的值是否重复,以及flavorName配置是否有效.并将配置的flavorName转换为英文全拼.
 3. 该列(除去第一行,第二行)每一行代表着一个*待添加的flavor*.

> 该步骤在内存中完成,不会对CSV文件做任何更改.

#### buildVariants
buildVariants的key为每一行(除第一第二行)flavorName列对应的单元格的值.

buildVariants的value为flavorName所在行的其余单元格组成的Map*( 该Map的key为所在单元格的标题,value为单元格的值 )*.



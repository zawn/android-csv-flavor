/*
 * Copyright 2015 ZhangZhenli <zhangzhenli@live.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.house365.build.util.CsvUtil
import com.house365.build.util.HanziToPinyin
import com.house365.build.util.URLtoFileUtil
import org.gradle.api.Project
import org.gradle.tooling.BuildException

/**
 * 读取渠道配置文件并返回.
 * <p/>
 * Created by ZhangZhenli on 2015/8/18.
 */
public class VariantsUtil {

    /**
     * 初始化字段buildVariants.
     * 首先尝试获取属性Property variantFileURL指定的URL,不存在则使用当前projectDIr下的variant.csv文件,
     *
     */
    static LinkedHashMap<String, LinkedHashMap<String, String>> readVariantsFromFile(Project project) {
        def preMillis = System.currentTimeMillis();
        LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> buildVariants = new LinkedHashMap<>()
        File file
        if (project.hasProperty("variantFileURL")) {
            String variantFileURL = project.getProperties().get("variantFileURL")
            try {
                URL url = new URL(variantFileURL)
                file = URLtoFileUtil.toFile(new URL(variantFileURL))
            } catch (MalformedURLException e) {
                file = new File(variantFileURL);
                if (!file.isAbsolute())
                    file = new File(project.projectDir, variantFileURL)
            }
        } else {
            file = new File(project.projectDir, "variant.csv")
        }

        if (file == null || !file.exists()) {
            throw new BuildException("Not specified channel configuration :\"-PvariantFileURL\", and does not find the default configuration file:" + file, new FileNotFoundException())
        }

        project.logger.lifecycle("Variants File :" + file.toPath().toAbsolutePath().toString())

        ArrayList<LinkedHashMap<String, String>> channels = CsvUtil.readCsvChannels(file, 2)
        channels = checkFlavorName(project, channels)
        for (LinkedHashMap<String, String> variant : channels) {
            String flavorName = variant.get("flavorName");
            if (variant.containsKey("enable")) {
                if (Boolean.parseBoolean(variant.get("enable"))) {
                    buildVariants.put(flavorName, variant);
                }
            } else {
                buildVariants.put(flavorName, variant);
            }
        }
        if (project.hasProperty("variantFileURL")) {
            file.delete();
        }
        println '读取配置文件耗时:' + (System.currentTimeMillis() - preMillis) + "毫秒"
        return buildVariants;
    }

/**
 * 检查渠道配置中flavorName设置是否正确.
 *
 * 用于检查渠道配置是否重复,以及渠道名称配置是否有效.并将配置的flavorName转换为英文全拼,如果没有配置则<p/>
 * 使用name或者第一列作为flavorName的数据源并做转换.
 * @param channels
 * @return
 */
    static ArrayList<LinkedHashMap<String, String>> checkFlavorName(Project project, ArrayList<LinkedHashMap<String, String>> channels) {
        ArrayList<LinkedHashMap<String, String>> checkedChannels = new ArrayList<>()
        LinkedHashSet<String> flavorNameSet = new LinkedHashSet<>();
        channels.each { LinkedHashMap<String, String> channel ->
            String flavorName
            if (channel.containsKey("flavorName")) {
                flavorName = channel.get("flavorName")
            } else {
                LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>()
                linkedHashMap.put("flavorName", "flavorName");
                linkedHashMap.putAll(channel);
                channel = linkedHashMap;
                if (channel.containsKey("name")) {
                    flavorName = channel.get("name")
                } else {
                    flavorName = channel.values().getAt(0)
                }
            }
            flavorName = HanziToPinyin.toPinYin(flavorName);
            channel.put("flavorName", flavorName);
            if (flavorNameSet.contains(flavorName))
                throw new BuildException("Duplicate channel configuration information: " + channel, new Exception());
            flavorNameSet.add(flavorName);
            checkedChannels.add(channel);
        }
        def taskNames = project.gradle.startParameter.taskNames
        if ("assembleRelease" in taskNames) {
            println "\n原始渠道配置信息:"
            CsvUtil.printlnCsv(channels);
            println "\n修正flavorName后的渠道配置信息:"
            CsvUtil.printlnCsv(checkedChannels);
        }
        return checkedChannels;
    }

}

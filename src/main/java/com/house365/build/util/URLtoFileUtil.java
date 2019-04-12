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

package com.house365.build.util;

import org.apache.tika.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * 从URL中读取文件.
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class URLtoFileUtil {

    public static File toFile(URL url, File parentFile) throws IOException {

        File tempFile = new File(parentFile, "android_variant.csv.tmp");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        IOUtils.copy(url.openStream(), new FileOutputStream(tempFile));
        tempFile.deleteOnExit();
        System.out.println("Copy: " + url + "\n to: " + tempFile);
        return tempFile;
    }

}

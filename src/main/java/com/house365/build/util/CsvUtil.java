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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;

import java.io.*;
import java.util.*;

/**
 * 读取渠道配置文件并返回.
 * <p>
 * Created by Administrator on 2015/8/18.
 */
public class CsvUtil {

    /**
     * 读取CSV文件.
     *
     * @param channelsFile
     * @param headerRow
     * @return 读取到的Csv信息
     * @throws FileNotFoundException
     */
    public static ArrayList<LinkedHashMap<String, String>> readCsvChannels(File channelsFile, int headerRow) throws Exception {
        return readCsvChannels(channelsFile, headerRow, false);
    }

    /**
     * 读取CSV文件.
     *
     * @param channelsFile
     * @param headerRow
     * @param isPrint      是否想控制台输出内容..
     * @return 读取到的Csv信息
     * @throws FileNotFoundException
     */
    public static ArrayList<LinkedHashMap<String, String>> readCsvChannels(File channelsFile, int headerRow, boolean isPrint) throws Exception {
        try {
            ArrayList<LinkedHashMap<String, String>> csvContent = new ArrayList<>();
            LinkedHashMap<String, String> rowContent;
            ArrayList<String> headerNames = null;
            if (headerRow > 0) {
                String[] csvHeader = getCsvHeader(channelsFile, headerRow);
                List<String> list = Arrays.asList(csvHeader);
                headerNames = new ArrayList<>(list);
            }
            AutoDetectReader reader = null;
            try {
                reader = new AutoDetectReader(new FileInputStream(channelsFile));
                Iterable<CSVRecord> csvRecords = CSVFormat.EXCEL.parse(reader);
                int i = 0;
                for (CSVRecord record : csvRecords) {
                    i++;
                    if (i <= headerRow) {
                        continue;
                    }
                    if (headerNames == null) {
                        headerNames = new ArrayList<>();
                        for (i = 0; i < record.size(); i++) {
                            headerNames.add(i + "");
                        }
                    }
                    rowContent = new LinkedHashMap<>();
                    for (i = 0; i < record.size(); i++) {
                        rowContent.put(headerNames.get(i), record.get(i));
                    }
                    csvContent.add(rowContent);
                }
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isPrint) {
                printlnCsv(csvContent);
            }
            return csvContent;
        } catch (IOException e) {
            throw e;
        } catch (TikaException e) {
            throw e;
        }
    }

    /**
     * 格式化输出.
     *
     * @param csvContent
     * @throws IOException
     */
    public static void printlnCsv(ArrayList<LinkedHashMap<String, String>> csvContent) throws IOException {
        TableBuilder tb = new TableBuilder();
        ArrayList<String> headerNames = new ArrayList<>();
        LinkedHashMap<String, String> linkedHashMap = csvContent.get(0);
        for (Map.Entry<String, String> entry : linkedHashMap.entrySet()) {
            headerNames.add(entry.getKey());
        }
        String[] names = new String[headerNames.size()];
        headerNames.toArray(names);
        tb.addRow(names);
        for (HashMap<String, String> mapRecord : csvContent) {
            names = new String[headerNames.size()];
            mapRecord.values().toArray(names);
            tb.addRow(names);
        }
        tb.toString();
        BufferedReader reader = new BufferedReader(new StringReader(tb.toString()));
        String line = reader.readLine();
        System.out.println(AnsiColor.ANSI_YELLOW + line + AnsiColor.ANSI_RESET);
        for (line = reader.readLine(); line != null; line = reader.readLine()) {
            System.out.println(AnsiColor.ANSI_GREEN + line + AnsiColor.ANSI_RESET);
        }
    }

    /**
     * 读取并返回CSV的标题行.
     *
     * @param csvFile
     * @param headerRow 标题所在的行数,从1开始
     * @return Csv标题数组
     */
    private static String[] getCsvHeader(File csvFile, int headerRow) throws Exception {
        String[] headNameStrings = null;
        AutoDetectReader reader = null;
        try {
            reader = new AutoDetectReader(new FileInputStream(csvFile));
            Iterable<CSVRecord> csvRecords = CSVFormat.EXCEL.parse(reader);
            int i = 1;
            for (CSVRecord record : csvRecords) {
                if (i == headerRow) {
                    headNameStrings = new String[record.size()];
                    for (int j = 0; j < record.size(); j++) {
                        headNameStrings[j] = record.get(j);
                    }
                    break;
                }
                i = i + 1;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception e) {
            }
        }
        return headNameStrings;
    }
}

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

import java.util.LinkedList;
import java.util.List;

/**
 * 格式化表格输出.
 *
 * @author ZhangZhenli <zhangzhenli@live.com>
 */
public class TableBuilder {

    List<String[]> rows = new LinkedList<String[]>();

    public void addRow(String... cols) {
        rows.add(cols);
    }

    private int[] colWidths() {
        int cols = -1;

        for (String[] row : rows) {
            cols = Math.max(cols, row.length);
        }
        int[] widths = new int[cols];
        for (String[] row : rows) {
            for (int colNum = 0; colNum < row.length; colNum++) {
                widths[colNum] = Math.max(widths[colNum], row[colNum].getBytes().length + 2);
            }
        }
        return widths;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        int[] colWidths = colWidths();
        for (String[] row : rows) {
            for (int colNum = 0; colNum < row.length; colNum++) {
                int j = colWidths[colNum] - row[colNum].getBytes().length;
                int i;
                for (i = 0; i < j / 2; i++) {
                    buf.append(" ");
                }
                buf.append(row[colNum]);
                for (i = row[colNum].getBytes().length + i; i < colWidths[colNum]; i++) {
                    buf.append(" ");
                }
                buf.append(' ');
            }
            buf.append('\n');
        }
        return buf.toString();
    }
}

package com.xxl.codegenerator.core.util;

import com.xxl.codegenerator.core.exception.CodeGenerateException;
import com.xxl.codegenerator.core.model.ClassInfo;
import com.xxl.codegenerator.core.model.FieldInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author xuxueli 2018-05-02 21:10:45
 */
public class TableParseUtil {

    /**
     * 解析建表SQL生成代码（model-dao-xml）
     *
     * @param tableSql
     * @return
     */
    public static ClassInfo processTableIntoClassInfo(String tableSql) throws IOException {
        if (tableSql==null || tableSql.trim().length()==0) {
            throw new CodeGenerateException("Table structure can not be empty.");
        }
        tableSql = tableSql.trim();

        // table Name
        String tableName = null;
        if (tableSql.contains("TABLE") && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf("TABLE")+5, tableSql.indexOf("("));
        } else if (tableSql.contains("table") && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf("table")+5, tableSql.indexOf("("));
        } else {
            throw new CodeGenerateException("Table structure anomaly.");
        }

        if (tableName.contains("`")) {
            tableName = tableName.substring(tableName.indexOf("`")+1, tableName.lastIndexOf("`"));
        }

        // class Name
        String className = StringUtils.upperCaseFirst(StringUtils.underlineToCamelCase(tableName));
        if (className.contains("_")) {
            className = className.replaceAll("_", "");
        }

        // class Comment
        String classComment = null;
        if (tableSql.contains("COMMENT=")) {
            String classCommentTmp = tableSql.substring(tableSql.lastIndexOf("COMMENT=")+8).trim();
            if (classCommentTmp.contains("'") || classCommentTmp.indexOf("'")!=classCommentTmp.lastIndexOf("'")) {
                classCommentTmp = classCommentTmp.substring(classCommentTmp.indexOf("'")+1, classCommentTmp.lastIndexOf("'"));
            }
            if (classCommentTmp!=null && classCommentTmp.trim().length()>0) {
                classComment = classCommentTmp;
            }
        }

        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();

        String fieldListTmp = tableSql.substring(tableSql.indexOf("(")+1, tableSql.lastIndexOf(")"));
        //此处要求必须SQL脚本按行分割
        String[] fieldLineList = fieldListTmp.split("\n");
        if (fieldLineList.length > 0) {
            for (String columnLine :fieldLineList) {
                columnLine = columnLine.trim();		                                        // `userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                if (columnLine.startsWith("`")){
                    // column Name
                    columnLine = columnLine.substring(1);			                        // userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String columnName = columnLine.substring(0, columnLine.indexOf("`"));	// userid

                    // field Name
                    String fieldName = StringUtils.lowerCaseFirst(StringUtils.underlineToCamelCase(columnName));
                    if (fieldName.contains("_")) {
                        fieldName = fieldName.replaceAll("_", "");
                    }

                    // field class
                    columnLine = columnLine.substring(columnLine.indexOf("`")+1).trim();	// int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String fieldClass = Object.class.getSimpleName();
                    if (columnLine.startsWith("int") || columnLine.startsWith("tinyint") || columnLine.startsWith("smallint")) {
                        fieldClass = Integer.TYPE.getSimpleName();
                    } else if (columnLine.startsWith("bigint")) {
                        fieldClass = Long.TYPE.getSimpleName();
                    } else if (columnLine.startsWith("float")) {
                        fieldClass = Float.TYPE.getSimpleName();
                    } else if (columnLine.startsWith("double")) {
                        fieldClass = Double.TYPE.getSimpleName();
                    } else if (columnLine.startsWith("datetime") || columnLine.startsWith("timestamp")) {
                        fieldClass = Date.class.getSimpleName();
                    } else if (columnLine.startsWith("varchar") || columnLine.startsWith("text")) {
                        fieldClass = String.class.getSimpleName();
                    } else if (columnLine.startsWith("decimal")) {
                        fieldClass = BigDecimal.class.getSimpleName();
                    }

                    // field comment
                    String fieldComment = null;
                    if (columnLine.contains("COMMENT")) {
                        int indexCom = columnLine.indexOf("COMMENT") + 7;
                        String commentTmp = fieldComment = columnLine.substring(indexCom).trim();	// '用户ID',
                        if (commentTmp.contains("'") || commentTmp.indexOf("'")!=commentTmp.lastIndexOf("'")) {
                            int firstIndex = commentTmp.indexOf("'")+1;
                            int lastIndex = commentTmp.lastIndexOf("'");
                            commentTmp = commentTmp.substring(firstIndex , lastIndex );
                        }
                        fieldComment = commentTmp;
                    }

                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setColumnName(columnName);
                    fieldInfo.setFieldName(fieldName);
                    fieldInfo.setFieldClass(fieldClass);
                    fieldInfo.setFieldComment(fieldComment);

                    fieldList.add(fieldInfo);
                }
            }
        }

        if (fieldList.size() < 1) {
            throw new CodeGenerateException("Table structure anomaly.");
        }

        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName(tableName);
        codeJavaInfo.setClassName(className);
        codeJavaInfo.setClassComment(classComment);
        codeJavaInfo.setFieldList(fieldList);

        return codeJavaInfo;
    }

}

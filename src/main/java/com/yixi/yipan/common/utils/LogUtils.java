package com.yixi.yipan.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogUtils {
    private static final Logger infoLogger = LoggerFactory.getLogger("infoLog");
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLog");
    private static final Logger warnLogger = LoggerFactory.getLogger("warnLog");

    /**
     * 记录信息日志
     * @param message 日志信息
     */
    public static void info(String message) {
        infoLogger.info(message);
    }

    /**
     * 记录信息日志（带参数）
     * @param format 日志格式
     * @param args 参数
     */
    public static void info(String format, Object... args) {
        infoLogger.info(format, args);
    }

    /**
     * 记录错误日志
     * @param message 错误信息
     * @param e 异常对象
     */
    public static void error(String message, Throwable e) {
        errorLogger.error(message, e);
    }

    /**
     * 记录错误日志
     * @param message 错误信息
     */
    public static void error(String message) {
        errorLogger.error(message);
    }

    /**
     * 记录警告日志
     * @param message 警告信息
     */
    public static void warn(String message) {
        warnLogger.warn(message);
    }

    /**
     * 记录警告日志（带参数）
     * @param format 日志格式
     * @param args 参数
     */
    public static void warn(String format, Object... args) {
        warnLogger.warn(format, args);
    }
}
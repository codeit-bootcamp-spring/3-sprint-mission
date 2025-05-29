package com.sprint.mission.discodeit.config;

import static org.springframework.util.StringUtils.hasText;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.engine.jdbc.internal.FormatStyle;

public class P6SpyPrettyFormatter implements MessageFormattingStrategy {

  // ANSI 색상 코드
  private static final String RESET = "\u001B[0m";
  private static final String YELLOW = "\u001B[33m"; // SQL 키워드
  private static final String CYAN = "\u001B[36m";   // 타이틀
  private static final String WHITE = "\u001B[37m";  // 기본 텍스트

  // SQL 키워드 패턴
  private static final Pattern SQL_KEYWORDS =
      Pattern.compile(
          "(?i)\\b(SELECT|INSERT|UPDATE|DELETE|FROM|WHERE|JOIN|ON|VALUES|SET|ORDER BY|GROUP BY|LIMIT|OFFSET|AS|AND|OR|INNER|LEFT|RIGHT|OUTER)\\b");

  @PostConstruct
  public void setLogMessageFormat() {
    P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
  }

  @Override
  public String formatMessage(int connectionId, String now, long elapsed, String category,
      String prepared, String sql, String url) {
    String formattedSql = formatSql(category, sql);
    return formatLog(elapsed, category, formattedSql);
  }

  private String formatSql(String category, String sql) {
    if (!hasText(sql) || !isStatement(category)) {
      return sql;
    }

    // 긴 binary 또는 values(...) 축약
    String compactSql = shortenLargeValues(sql);

    String trimmedSQL = trim(compactSql);
    String formatted =
        isDdl(trimmedSQL) ? FormatStyle.DDL.getFormatter().format(compactSql)
            : FormatStyle.BASIC.getFormatter().format(compactSql);

    return highlightKeywords(formatted);
  }

  private static final Pattern BYTE_LITERAL_PATTERN =
      Pattern.compile("'[0-9A-Fa-f]{100,}'"); // 100자 이상 hex literal

  private String shortenLargeValues(String sql) {
    Matcher matcher = BYTE_LITERAL_PATTERN.matcher(sql);
    StringBuffer sb = new StringBuffer();

    while (matcher.find()) {
      matcher.appendReplacement(sb, "'[long value...]'");
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  private String highlightKeywords(String sql) {
    Matcher matcher = SQL_KEYWORDS.matcher(sql);
    StringBuffer sb = new StringBuffer();

    while (matcher.find()) {
      String keyword = matcher.group().toUpperCase();
      matcher.appendReplacement(sb, YELLOW + keyword + RESET);
    }

    matcher.appendTail(sb);
    return WHITE + sb.toString() + RESET;
  }

  private String formatLog(long elapsed, String category, String formattedSql) {
    return String.format(
        "%s[SQL 실행]%s %s\n%s[실행 시간]: %d ms%s\n",
        CYAN, RESET, category, YELLOW, elapsed, RESET
    ) + formattedSql + "\n";
  }

  private static boolean isDdl(String trimmedSQL) {
    return trimmedSQL.startsWith("create") || trimmedSQL.startsWith("alter")
        || trimmedSQL.startsWith("comment") || trimmedSQL.startsWith("drop");
  }

  private static String trim(String sql) {
    return sql.trim().toLowerCase(Locale.ROOT);
  }

  private static boolean isStatement(String category) {
    return Category.STATEMENT.getName().equals(category);
  }
}

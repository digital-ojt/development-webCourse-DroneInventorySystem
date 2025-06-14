package com.digitalojt.web.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * エラーレスポンスを格納するクラス
 *
 * @author dotlife
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private String message;
  private int status;
  private LocalDateTime timestamp;
}

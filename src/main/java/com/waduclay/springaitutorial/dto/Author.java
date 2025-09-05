package com.waduclay.springaitutorial.dto;


import java.util.List;

/**
 * @author <a href="mailto:developer.wadu@gmail.com">Willdom Kahari</a>
 */
public record Author(String author, List<String> books  ) {
}

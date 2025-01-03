package com.rabbit.foot.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.rabbit.foot.utils.ConvertUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * <p>
 * 读取GitHub中的文件内容
 * </p>
 *
 * @author LiAo
 * @since 2023-11-30
 */
public class GitHubFileReader {
    private static final Logger logger = Logger.getLogger(GitHubFileReader.class.getName());
    public static final String GITHUB_API_URL = "https://api.github.com/repos/%s/%s/contents/%s";

    /**
     * 读取GitHub指定仓库的指定文件夹
     *
     * @param owner 用户名
     * @param repo  仓库名称
     * @param path  文件路径
     * @return 文件内容
     */
    public static String getFileContent(String owner, String repo, String path) {
        return getFileContent(owner, repo, path, null);
    }

    /**
     * 读取GitHub指定仓库的指定文件夹
     *
     * @param owner       用户名
     * @param repo        仓库名称
     * @param path        文件路径
     * @param githubToken 用户Token
     * @return 文件内容
     */
    public static String getFileContent(String owner, String repo, String path, String githubToken) {
        try {
            String apiUrl = String.format(GITHUB_API_URL, owner, repo, path);
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 设置 GitHub API 请求头，如果你使用了 token，请将 "YOUR_GITHUB_TOKEN" 替换成实际的 token
            if (githubToken != null && !githubToken.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + githubToken);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // 解析 JSON 获取文件内容
                return parseFileContent(response.toString());
            } else {
                logger.warning("检索GitHub文件失败。响应代码: " + responseCode);
            }
        } catch (IOException ignored) {
        }

        return null;
    }

    /**
     * 读取 github 返回的文件内容，并解密Base64
     *
     * @param jsonResponse github返回的内容
     * @return 文件内容
     */
    private static String parseFileContent(String jsonResponse) {
        JsonNode jsonNode = ConvertUtils.jsonStrToJsonNode(jsonResponse);
        String base64Content = jsonNode.get("content").asText();
        base64Content = base64Content.replaceAll("\\\\n", "");
        byte[] decodedBytes;

        try {
            decodedBytes = Base64.getMimeDecoder().decode(base64Content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}

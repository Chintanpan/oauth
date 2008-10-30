/*
 * Copyright 2008 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.oauth.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;

/**
 * An HttpServletRequest, encapsulated as an OAuthMessage.
 * 
 * @author John Kristian
 */
public class HttpRequestMessage extends OAuthMessage {

    public HttpRequestMessage(HttpServletRequest request, String URL) {
        super(request.getMethod(), URL, getParameters(request));
        this.request = request;
    }

    private final HttpServletRequest request;

    @Override
    public InputStream getBodyAsStream() throws IOException {
        return request.getInputStream();
    }

    @Override
    public String getBodyAsString() throws IOException {
        return readAll(getBodyAsStream(), getContentCharset());
    }

    @Override
    public String getContentCharset() {
        return request.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    public static List<OAuth.Parameter> getParameters(HttpServletRequest request) {
        List<OAuth.Parameter> list = new ArrayList<OAuth.Parameter>();
        for (Enumeration headers = request.getHeaders("Authorization"); headers != null
                && headers.hasMoreElements();) {
            String header = headers.nextElement().toString();
            for (OAuth.Parameter parameter : OAuthMessage
                    .decodeAuthorization(header)) {
                if (!parameter.getKey().equalsIgnoreCase("realm")) {
                    list.add(parameter);
                }
            }
        }
        for (Object e : request.getParameterMap().entrySet()) {
            Map.Entry entry = (Map.Entry) e;
            String name = entry.getKey().toString();
            for (String value : (String[]) entry.getValue()) {
                list.add(new OAuth.Parameter(name, value));
            }
        }
        return list;
    }

}

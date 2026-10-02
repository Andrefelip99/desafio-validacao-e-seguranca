package com.devsuperior.bds04.tests;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class TokenUtil {

	@Value("${security.client-id}")
	private String clientId;

	@Value("${security.client-secret}")
	private String clientSecret;
	
	public String obtainAccessToken(MockMvc mockMvc, String username, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", clientId);
		params.add("username", username);
		params.add("password", password);

		MvcResult result = mockMvc
				.perform(post("/oauth2/token")
						.params(params)
						.with(httpBasic(clientId, clientSecret))
						.accept("application/json;charset=UTF-8"))
						.andDo(print())
						.andReturn();
		if (result.getResponse().getStatus() != 200) {
			throw new IllegalStateException("Falha ao obter token (HTTP "
					+ result.getResponse().getStatus() + "): "
					+ result.getResponse().getContentAsString());
		}
		if (!"application/json;charset=UTF-8".equals(result.getResponse().getContentType())) {
			throw new IllegalStateException("Content-Type inesperado ao obter token: "
					+ result.getResponse().getContentType());
		}

		String resultString = result.getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}
}

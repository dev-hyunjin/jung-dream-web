/**
 * Java 의 단방향 암호화와 양방향 암복호화를 돕는 라이브러리
 * application.properties 의 데이터베이스 비밀번호 암호화, ssl 인증스 암호화 등을 위해 사용함
 */
package com.app.jungdreamweb.config;

import com.app.jungdreamweb.util.Aes;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableEncryptableProperties
@Configuration
public class JasyptConfig {

	@Value("${jasypt.encryptor.key}")
	private String aesEncryptorKey;

	@Bean("jasyptStringEncryptor")
	StringEncryptor databaseStringEncryptor() {

		String encryptorKey = Aes.getInstance().decryptString(aesEncryptorKey);

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();
        config.setPassword(encryptorKey); // 환경 변수에서 마스터 키 가져오기
        encryptor.setConfig(config);
        return encryptor;

    }
}

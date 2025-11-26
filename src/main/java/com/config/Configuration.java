/*
 * Apache 2.0 License
 *
 * Copyright (c) 2022 TestLeaf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;

@LoadPolicy(LoadType.MERGE)
@Config.Sources({
	"system:properties",
	"classpath:local.properties",
	"classpath:app.properties",
"classpath:report.properties"})
public interface Configuration extends Config {

	@Key("app.clientName")
	String appClientName();

	@Key("app.username.tier1_Admin")
	String appUserNameTier1Admin();

	@Key("app.password.tier1_Admin")
	String appPasswordTier1Admin();

	@Key("app.username.tier1_User")
	String appUserNameTier1User();

	@Key("app.password.tier1_User")
	String appPasswordTier1User();


	@Key("app.username.tier2_Admin")
	String appUserNameTier2Admin();

	@Key("app.password.tier2_Admin")
	String appPasswordTier2Admin();

	@Key("app.username.tier2_User")
	String appUserNameTier2User();

	@Key("app.password.tier2_User")
	String appPasswordTier2User();

	@Key("app.username.tier3_EndUser")
	String appUserNameTier3EndUser();

	@Key("app.password.tier3_EndUser")
	String appPasswordTier3EndUser();


	@Key("target")
	String target();

	@Key("browser")
	String browser();

	@Key("headless")
	Boolean headless();

	@Key("url.api")
	String apiUrl();

	@Key("url.staging")
	String baseStagingUrl();

	@Key("baseUrl.staging")
	String baseUrlStaging();
	
	@Key("url.uat")
	String baseUatUrl();
	
	@Key("client.id")
	String clientId();
	
	@Key("client.secret")
	String clientSecret();
	
	@Key("url.oauth")
	String oauthUrl();	

	@Key("timeout")
	int timeout();
	
	@Key("beneficiaryTimeout")
	int beneficiaryTimeout();

	@Key("grid.url")
	String gridUrl();

	@Key("grid.port")
	String gridPort();

	@Key("faker.locale")
	String faker();

	@Key("auto.login")
	boolean autoLogin();

	@Key("enable.tracing")
	boolean enableTracing();
	
	@Key("action.delay")
	double slowMotion();

	@Key("pause.low")
	long pauseLow();

	@Key("pause.medium")
	long pauseMedium();

	@Key("pause.high")
	long pauseHigh();

	@Key("email.max.timeout")
	int maxEmailTimeout();

	@Key("max.retry")
	int maxRetry();

	@Key("report.title")
	String reportTitle();

	@Key("report.name")
	String reportName();
	
	@Key("report.theme")
	String reportTheme();




}

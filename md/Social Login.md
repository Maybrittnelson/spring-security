# Social Login	

[Spring Social](https://github.com/spring-projects/spring-social)源码级讲解,讲解以QQ登录为案例。

## Social Authentication Filter(子类)

### Abstract Authentication Processing Filter(父类)

do Filter(父) => attempt Authentication(子) => attempt AuthService(子)

```java
	/*
	 * Call SocialAuthenticationService.getAuthToken() to get SocialAuthenticationToken:
	 *     If first phase, throw AuthenticationRedirectException to redirect to provider website.
	 *     If second phase, get token/code from request parameter and call provider API to get accessToken/accessGrant.
	 * Check Authentication object in spring security context, if null or not authenticated,  call doAuthentication()
	 * Otherwise, it is already authenticated, add this connection.
	 */
	private Authentication attemptAuthService(final SocialAuthenticationService<?> authService, final HttpServletRequest request, HttpServletResponse response) 
			throws SocialAuthenticationRedirectException, AuthenticationException {
		//OAuth2AuthenticationService是执行前5步
		final SocialAuthenticationToken token = authService.getAuthToken(request, response);
		if (token == null) return null;
		
		Assert.notNull(token.getConnection());
		
		Authentication auth = getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
          	//封装auth成功
			return doAuthentication(authService, request, token);
		} else {
			addConnection(authService, request, token, auth);
			return null;
		}		
	}	
```

```java
	private Authentication doAuthentication(SocialAuthenticationService<?> authService, HttpServletRequest request, SocialAuthenticationToken token) {
		try {
			if (!authService.getConnectionCardinality().isAuthenticatePossible()) return null;
			token.setDetails(authenticationDetailsSource.buildDetails(request));
          	//AuthenticationManager(ProviderManager是前者的实现类)
			Authentication success = getAuthenticationManager().authenticate(token);
			Assert.isInstanceOf(SocialUserDetails.class, success.getPrincipal(), "unexpected principle type");
			updateConnections(authService, token, success);			
			return success;
		} catch (BadCredentialsException e) {
			// connection unknown, register new user?
			if (signupUrl != null) {
				// store ConnectionData in session and redirect to register page
				sessionStrategy.setAttribute(new ServletWebRequest(request), ProviderSignInAttempt.SESSION_ATTRIBUTE, new ProviderSignInAttempt(token.getConnection()));
				throw new SocialAuthenticationRedirectException(buildSignupUrl(request));
			}
			throw e;
		}
	}

```



###OAuth2AuthenticationService

```java
/*
	上一方法的注释：第一次没有授权码时，重定向到第三方认证服务器
					第二次获取验证码，发送获取access token
						利用adapter将api中的信息set到connection
*/
public SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) throws SocialAuthenticationRedirectException {
		String code = request.getParameter("code");
		if (!StringUtils.hasText(code)) {
			OAuth2Parameters params =  new OAuth2Parameters();
			params.setRedirectUri(buildReturnToUrl(request));
			setScope(request, params);
			params.add("state", generateState(connectionFactory, request));
			addCustomParameters(params);
			throw new SocialAuthenticationRedirectException(getConnectionFactory().getOAuthOperations().buildAuthenticateUrl(params));
		} else if (StringUtils.hasText(code)) {
			try {
				String returnToUrl = buildReturnToUrl(request);
				AccessGrant accessGrant = getConnectionFactory().getOAuthOperations().exchangeForAccess(code, returnToUrl, null);
				// TODO avoid API call if possible (auth using token would be fine)
				Connection<S> connection = getConnectionFactory().createConnection(accessGrant);
				return new SocialAuthenticationToken(connection, null);
			} catch (RestClientException e) {
				logger.debug("failed to exchange for access", e);
				return null;
			}
		} else {
			return null;
		}
	}
```



### Provider Manager

```java
	/**
	 * Attempts to authenticate the passed {@link Authentication} object.
	 * <p>
	 * The list of {@link AuthenticationProvider}s will be successively tried until an
	 * <code>AuthenticationProvider</code> indicates it is capable of authenticating the
	 * type of <code>Authentication</code> object passed. Authentication will then be
	 * attempted with that <code>AuthenticationProvider</code>.
	 * <p>
	 * If more than one <code>AuthenticationProvider</code> supports the passed
	 * <code>Authentication</code> object, the first one able to successfully
	 * authenticate the <code>Authentication</code> object determines the
	 * <code>result</code>, overriding any possible <code>AuthenticationException</code>
	 * thrown by earlier supporting <code>AuthenticationProvider</code>s.
	 * On successful authentication, no subsequent <code>AuthenticationProvider</code>s
	 * will be tried.
	 * If authentication was not successful by any supporting
	 * <code>AuthenticationProvider</code> the last thrown
	 * <code>AuthenticationException</code> will be rethrown.
	 *
	 * @param authentication the authentication request object.
	 *
	 * @return a fully authenticated object including credentials.
	 *
	 * @throws AuthenticationException if authentication fails.
	 */
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		Class<? extends Authentication> toTest = authentication.getClass();
		AuthenticationException lastException = null;
		Authentication result = null;
		boolean debug = logger.isDebugEnabled();

		for (AuthenticationProvider provider : getProviders()) {
			if (!provider.supports(toTest)) {
				continue;
			}

			if (debug) {
				logger.debug("Authentication attempt using "
						+ provider.getClass().getName());
			}

			try {
              	//SocialAuthenticationProvider
				result = provider.authenticate(authentication);

				if (result != null) {
					copyDetails(authentication, result);
					break;
				}
			}
			catch (AccountStatusException e) {
				prepareException(e, authentication);
				// SEC-546: Avoid polling additional providers if auth failure is due to
				// invalid account status
				throw e;
			}
			catch (InternalAuthenticationServiceException e) {
				prepareException(e, authentication);
				throw e;
			}
			catch (AuthenticationException e) {
				lastException = e;
			}
		}

		if (result == null && parent != null) {
			// Allow the parent to try.
			try {
				result = parent.authenticate(authentication);
			}
			catch (ProviderNotFoundException e) {
				// ignore as we will throw below if no other exception occurred prior to
				// calling parent and the parent
				// may throw ProviderNotFound even though a provider in the child already
				// handled the request
			}
			catch (AuthenticationException e) {
				lastException = e;
			}
		}

		if (result != null) {
			if (eraseCredentialsAfterAuthentication
					&& (result instanceof CredentialsContainer)) {
				// Authentication is complete. Remove credentials and other secret data
				// from authentication
				((CredentialsContainer) result).eraseCredentials();
			}

			eventPublisher.publishAuthenticationSuccess(result);
			return result;
		}

		// Parent was null, or didn't authenticate (or throw an exception).

		if (lastException == null) {
			lastException = new ProviderNotFoundException(messages.getMessage(
					"ProviderManager.providerNotFound",
					new Object[] { toTest.getName() },
					"No AuthenticationProvider found for {0}"));
		}

		prepareException(lastException, authentication);

		throw lastException;
	}
```



### Social Authentication Provider

```java
/**
 * Authenticate user based on {@link SocialAuthenticationToken}
 */
public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	Assert.isInstanceOf(SocialAuthenticationToken.class, authentication, "unsupported authentication type");
	Assert.isTrue(!authentication.isAuthenticated(), "already authenticated");
	SocialAuthenticationToken authToken = (SocialAuthenticationToken) authentication;
	String providerId = authToken.getProviderId();
	Connection<?> connection = authToken.getConnection();
	//查询数据库中是否唯一的userId 并且如果无 还插入值
	String userId = toUserId(connection);
	if (userId == null) {
		throw new BadCredentialsException("Unknown access token");
	}
	//根据userId查询*_userconnection数据
	UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
	if (userDetails == null) {
		throw new UsernameNotFoundException("Unknown connected account id");
	}

	return new SocialAuthenticationToken(connection, userDetails, authToken.getProviderAccountData(), getAuthorities(providerId, userDetails));
}

protected String toUserId(Connection<?> connection) {
  	//可插入值
	List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
	// only if a single userId is connected to this providerUserId
	return (userIds.size() == 1) ? userIds.iterator().next() : null;
}
```


### Jdbc Users Connection Repository(子)

#### Users Connection Repository(父)

```java
	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		ConnectionKey key = connection.getKey();
		List<String> localUserIds = jdbcTemplate.queryForList("select userId from " + tablePrefix + "UserConnection where providerId = ? and providerUserId = ?", String.class, key.getProviderId(), key.getProviderUserId());
      	//如果对应系统下的opendId无记录, 且connnectionSignUp不为空
		if (localUserIds.size() == 0 && connectionSignUp != null) {
			String newUserId = connectionSignUp.execute(connection);
			if (newUserId != null)
			{
				createConnectionRepository(newUserId).addConnection(connection);
				return Arrays.asList(newUserId);
			}
		}
		return localUserIds;
	}
	
		/**
			sql含义：
				userId以手机号为例,providerId为某个系统下对应某个的社交路径
			rank表示比如一个手机号下 pos对应多个QQ号
		**/
		@Transactional
	public void addConnection(Connection<?> connection) {
		try {
			ConnectionData data = connection.createData();
			int rank = jdbcTemplate.queryForObject("select coalesce(max(rank) + 1, 1) as rank from " + tablePrefix + "UserConnection where userId = ? and providerId = ?", new Object[]{ userId, data.getProviderId() }, Integer.class);
			jdbcTemplate.update("insert into " + tablePrefix + "UserConnection (userId, providerId, providerUserId, rank, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					userId, data.getProviderId(), data.getProviderUserId(), rank, data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime());
		} catch (DuplicateKeyException e) {
			throw new DuplicateConnectionException(connection.getKey());
		}
	}
```



### Demo Connection Sign Up

#### Connection Sign Up(父接口)

```java
/**
 * @author ShaoFeng
 * 作用  授权后 自动注册
 * @see org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository.findUserIdsWithConnection(Connection<?>)
 * 上述方法判断 ConnectionSignUp不为空
 * 则自动执行 自动注册
 */
@Component
public class DemoConnectionSignUp implements ConnectionSignUp {

	@Override
	public String execute(Connection<?> connection) {
		//根据社交用户信息默认创建用户并返回用户唯一标识
		return connection.getDisplayName();
	}

}
```


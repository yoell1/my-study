<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

	<jsp:include page="/WEB-INF/views/common/header.jsp" />

	<h2 class="page-title">로그인</h2>

	<c:if test="${param.error != null}">
		<p class="form-tip">아이디 또는 비밀번호가 일치하지 않습니다.</p>
	</c:if>

	<form id="login-form" class="form form-flex" action="/member/login" method="post">
		<div class="form-row">
			<label for="member-id">아이디</label>
			<input type="text" id="member-id" name="memberId" required autocomplete="off">
		</div>

		<div class="form-row">
			<label for="member-pwd">비밀번호</label>
			<input type="password" id="member-pwd" name="memberPwd" required>
		</div>

		<div class="form-row">
			<button type="submit" class="btn btn-primary">로그인</button>
		</div>
	</form>

	<jsp:include page="/WEB-INF/views/common/footer.jsp" />
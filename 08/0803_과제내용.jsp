<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ page import="java.util.List,java.util.ArrayList"%>
<%@ page import="com.kh.mvc.model.MemberDTO"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 목록</title>
</head>
<body>
	<h1>회원 목록</h1>
	<script> 
  function deleteMember(id) { 
    if(confirm(id + "번 회원을 정말 삭제하시겠습니까?")) {         
        location.href = "<c:url value='/member/delete'/>?id=" + id; 
    } 
} 
function updateMember(id) { 
    location.href = "<c:url value='/member/update'/>?id=" + id; 
} 
</script>
	<a href="<c:url value='/member/insert.html'/>">회원 등록</a>

	<table border="1">
		<thead>
			<tr>
				<th>회원 번호</th>
				<th>이름</th>
				<th>이메일</th>
				<th>나이</th>
				<th>수정</th>
				<th>삭제</th>
			</tr>
		</thead>
		<tbody>

			<c:forEach var="m" items="${memberList}">
				<tr>

					<td>${m.id}</td>
					<td>${m.name}</td>
					<td>${m.email}</td>
					<td>${m.age}</td>
					<td>

						<button type="button" onclick="updateMember(${m.id})">수정</button>
					</td>
					<td>
						<button type="button" onclick="deleteMember(${m.id})">삭제</button>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>

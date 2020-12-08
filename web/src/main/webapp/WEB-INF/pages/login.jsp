<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<tags:master pageTitle="Login">

    <style>
        .login-form {
            width: 340px;
            margin: 50px auto;
        }

        .login-form form {
            margin-bottom: 15px;
            padding: 30px;
        }

        .login-form h2 {
            margin: 0 0 15px;
        }

        .form-control, .btn {
            min-height: 38px;
            border-radius: 2px;
        }

        .btn {
            font-size: 15px;
            font-weight: bold;
        }
    </style>

    <c:if test="${not empty loginError}">
        <div class="alert alert-danger" role="alert">
                ${loginError}
        </div>
    </c:if>
    <div class="login-form">
        <form class="form-container" action="${pageContext.request.contextPath}/login" method="post">
            <h1 style="text-align: center">Log in</h1>
            <input class="form-control" type="text" name="username" placeholder="User name" required>
            <br/>
            <input class="form-control" type="password" name="password" placeholder="Password" required>
            <br/>
            <div class="form-group">
                <button type="submit" class="btn btn-outline-dark btn-block">Log in</button>
            </div>
        </form>
    </div>
</tags:master>
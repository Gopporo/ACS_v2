<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <title>Course project by Sasha Popov</title>
</head>
<body>
<header>
    <#include "components/navbar.ftl">
</header>
<main class="d-grid gap-5 py-5 mx-auto" style="width: 900px;">
    <h2 style="text-align: center">Информация о пользователе</h2>
    <hr>
    <div style="text-align: center">
        <h3>${user.name}</h3>
        <br>
        Отдел: <b>${user.department.name}</b>
        <br>
        Должность: <b>${user.position}</b>
        <br>
        Номер телефона: <b>${user.number_phone}</b>
        <br>
        Почта:<b>${user.email}</b>
        <br>
        Уровень доступа:<b>${user.userAccessLvl}</b>
        <br>
        <form action="/admin/changeRole/${user.id}" method="post" class="needs-validation" novalidate>
            <input type="hidden" name="userId" value="${user.id}">
            <div class="mb-3">
                <label for="roles" class="form-label">Роль</label>
                <select class="form-select" id="roles" name="role" required>
                    <#list roles as role>
                        <option value="${role.name()}" <#if user.roles?seq_contains(role)>selected</#if>>${role.name()}</option>
                    </#list>
                </select>
            </div>
            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <div class="text-center">
                <button type="submit" class="btn btn-primary">Сохранить</button>
            </div>
        </form>
    </div>
    <#if errorMessage??>
        <h2 style="color: red">${errorMessage}</h2>
    </#if>
</main>
</body>
</html>
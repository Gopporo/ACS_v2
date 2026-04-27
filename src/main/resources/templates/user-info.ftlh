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
        Отдел: <b>${(user.department.name)!'-'}</b>
        <br>
        Должность: <b>${user.position}</b>
        <br>
        Номер телефона: <b>${user.number_phone}</b>
        <br>
        Почта: <b>${user.email}</b>
        <br>
        Уровень доступа: <b>${user.userAccessLvl?string?replace("LEVEL_", "Уровень ")}</b>
        <hr>
        <a class="btn btn-primary"
           href="
<#if role??>
    <#switch role>
        <#case "ROLE_DIRECTOR">
        /director/users
        <#break>
        <#case "ROLE_ADMIN">
        /admin/users
        <#break>
    </#switch>
</#if>
">
            Назад
        </a>
        <br>
    </div>
    <#if errorMessage??>
        <h2 style="color: red">${errorMessage}</h2>
    </#if>
</main>
</body>
</html>
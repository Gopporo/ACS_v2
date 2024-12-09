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
    <h1 style="text-align: center">Отчет</h1>
    <hr>
    <div style="text-align: center">
        <h3>${report.application.name}</h3>
        <br>
        Сотрудник: <b>${report.application.user.name}</b>
        <br>
        Создан: <b>${report.createdAtFormatted}</b>
        <br>
        <p>${report.reportDisc}</p>
        <hr>
        <a class="btn btn-primary" href="/director/reports">Назад</a>
        <br>
    </div>
    <#if errorMessage??>
        <h2 style="color: red">${errorMessage}</h2>
    </#if>
</main>
</body>
</html>

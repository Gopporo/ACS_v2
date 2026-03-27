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
    <h2 style="text-align: center">Информация о заявке</h2>
    <hr>
    <div style="text-align: center">
        <h3>${application.name}</h3>
        <br>
        Зона выполнения: <b>${application.zone.name}</b>
        <br>
        Уровень доступа: <b>${application.accessLevel?string?replace("LEVEL_", "Уровень ")}</b>
        <br>
        Описание:<b>${application.disc}</b>
        <br>
        <form action="/employee/addReport" method="post">
            <input type="hidden" name="applicationId" value="${application.id}">

            <div class="mb-3">
                <label for="content" class="form-label">Напишите отчет:</label>
                <textarea
                        id="content"
                        name="reportDisc"
                        class="form-control"
                        rows="10"
                        placeholder="Напишите отчет"
                        required></textarea>
            </div>

            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button type="submit" class="btn btn-success mt-2">Отправить отчет</button>
        </form>

        <br>
    </div>
    <#if errorMessage??>
        <h2 style="color: red">${errorMessage}</h2>
    </#if>
</main>
</body>
</html>

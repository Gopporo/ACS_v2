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
    <h2 style="text-align: center">Редактирование заявки</h2>
    <hr>
    <div style="text-align: center">
        <form action="/director/updateApplication" method="post">
            <input type="hidden" name="applicationId" value="${application.id}">

            <div class="mb-3">
                <label for="name" class="form-label">Название:</label>
                <input
                        type="text"
                        id="name"
                        name="name"
                        class="form-control"
                        value="${application.name}"
                        required>
            </div>

            <div class="mb-3">
                <label for="disc" class="form-label">Описание:</label>
                <input
                        type="text"
                        id="disc"
                        name="disc"
                        class="form-control"
                        value="${application.disc}"
                        required>
            </div>

            <div class="mb-3">
                <label for="zone" class="form-label">Зона выполнения:</label>
                <select id="zone" name="zoneId" class="form-select">
                    <#list zones as zone>
                        <option value="${zone.id}" <#if zone.id == application.zone.id>selected</#if>>
                            ${zone.name}
                        </option>
                    </#list>
                </select>
            </div>

            <input type="hidden" name="_csrf" value="${_csrf.token}">
            <button type="submit" class="btn btn-success mt-2">Сохранить</button>
        </form>

        <br>
    </div>
    <#if errorMessage??>
        <h2 style="color: red">${errorMessage}</h2>
    </#if>
</main>
</body>
</html>

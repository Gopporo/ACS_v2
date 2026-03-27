<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Редактировать зону (Security)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4" style="max-width: 700px;">
    <h1 class="h3 mb-3">Редактирование зоны доступа</h1>

    <form action="/security/doors/update/${door.id}" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}">

        <div class="mb-3">
            <label class="form-label">Название зоны</label>
            <input type="text" class="form-control" name="name" value="${door.name!''}" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Уровень доступа</label>
            <select class="form-select" name="zoneAccessLvl" required>
                <#list accessLevels as lvl>
                    <option value="${lvl}" <#if door.zoneAccessLvl?? && door.zoneAccessLvl == lvl>selected</#if>>${lvl}</option>
                </#list>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Описание зоны</label>
            <input type="text" class="form-control" name="disc" value="${door.disc!''}">
        </div>

        <button type="submit" class="btn btn-dark">Сохранить</button>
        <a class="btn btn-outline-danger ms-2" href="/security/doors/delete/${door.id}">Удалить</a>
    </form>
</main>

<#include "components/footer.ftl">
</body>
</html>


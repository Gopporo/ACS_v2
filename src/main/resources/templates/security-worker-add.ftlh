<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Добавить сотрудника (Security)</title>
    <link rel="stylesheet" href="/css/filters.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4" style="max-width: 700px;">
    <h1 class="h3 mb-3">Добавление сотрудника</h1>

    <form action="/security/workers/add" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}">

        <div class="mb-3">
            <label class="form-label">Имя</label>
            <input type="text" class="form-control" name="firstName" value="${worker.firstName!''}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Фамилия</label>
            <input type="text" class="form-control" name="lastName" value="${worker.lastName!''}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Отчество</label>
            <input type="text" class="form-control" name="surname" value="${worker.surname!''}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Номер телефона</label>
            <input type="tel" class="form-control" name="numberPhone" value="${worker.numberPhone!''}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Уровень доступа</label>
            <select class="form-select" name="userAccessLvl" required>
                <#list accessLevels as lvl>
                    <option value="${lvl}" <#if worker.userAccessLvl?? && worker.userAccessLvl == lvl>selected</#if>>
                        ${lvl?string?replace("LEVEL_", "Уровень ")}
                    </option>
                </#list>
            </select>
        </div>

        <button type="submit" class="btn btn-dark">Добавить</button>
    </form>
</main>

<#include "components/footer.ftl">
</body>
</html>


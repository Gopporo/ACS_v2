<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Зоны (Security)</title>
    <link rel="stylesheet" href="/css/filters.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h3 mb-0">Зоны доступа</h1>
        <a class="btn btn-dark" href="/security/doors/add">Добавить</a>
    </div>

    <form method="get" action="/security/doors" class="row g-2 align-items-end mb-4">
        <div class="col-md-4">
            <label class="form-label">Уровень доступа зоны</label>
            <select name="accessLevel" class="form-select">
                <option value="" <#if !(accessLevel??) || accessLevel == "">selected</#if>>Все</option>
                <#list accessLevels as lvl>
                    <option value="${lvl}" <#if accessLevel?? && accessLevel == lvl?string>selected</#if>>${lvl}</option>
                </#list>
            </select>
        </div>
        <div class="col-auto">
            <button class="btn btn-dark" type="submit">Фильтр</button>
        </div>
    </form>

    <div class="table-responsive">
        <table class="table table-bordered align-middle">
            <thead class="table-light">
            <tr>
                <th>Название</th>
                <th>Уровень доступа</th>
                <th style="width: 220px;">Действия</th>
            </tr>
            </thead>
            <tbody>
            <#list doors as door>
                <tr>
                    <td>${door.name!""}</td>
                    <td>
                        <#if door.zoneAccessLvl??>
                            ${door.zoneAccessLvl?string?replace("LEVEL_", "Уровень ")}
                        <#else>-</#if>
                    </td>
                    <td class="d-flex gap-2">
                        <a class="btn btn-outline-dark btn-sm" href="/security/doors/edit/${door.id}">Редактировать</a>
                        <a class="btn btn-outline-danger btn-sm" href="/security/doors/delete/${door.id}">Удалить</a>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</main>

<#include "components/footer.ftl">
</body>
</html>


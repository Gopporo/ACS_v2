<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редактирование зоны</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4" style="max-width: 700px;">
    <h1 class="h3 mb-3">Редактирование зоны</h1>
    <form action="/admin/updateZone/${zone.id}" method="post">
        <div class="mb-3">
            <label class="form-label">Название зоны</label>
            <input name="name" type="text" class="form-control" value="${zone.name!''}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Описание</label>
            <input name="disc" type="text" class="form-control" value="${zone.disc!''}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Уровень доступа</label>
            <select name="zoneAccessLvl" class="form-select" required>
                <#list accessLevels as lvl>
                    <option value="${lvl}" <#if zone.zoneAccessLvl?? && zone.zoneAccessLvl == lvl>selected</#if>>${lvl?string?replace("LEVEL_", "Уровень ")}</option>
                </#list>
            </select>
        </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit" class="btn btn-dark">Сохранить</button>
        <a href="/admin/zones" class="btn btn-outline-secondary ms-2">Отмена</a>
    </form>
</main>

<#include "components/footer.ftl">
</body>
</html>

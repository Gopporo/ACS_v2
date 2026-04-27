<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>События доступа (Security)</title>
    <link rel="stylesheet" href="/css/filters.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h3 mb-0">События доступа</h1>
    </div>

    <div class="mb-3 d-flex gap-2 flex-wrap">
        <a class="btn btn-outline-dark <#if !(success??)>active</#if>" href="/security/accessAttempts">Все</a>
        <a class="btn btn-outline-success <#if success?? && success>active</#if>" href="/security/accessAttempts?success=true">Санкционированный</a>
        <a class="btn btn-outline-danger <#if success?? && !success>active</#if>" href="/security/accessAttempts?success=false">Несанкционированный</a>
    </div>

    <form method="get" action="/security/accessAttempts" class="row g-2 align-items-end mb-3">
        <div class="col-md-6">
            <label class="form-label">Сотрудник</label>
            <select class="form-select" name="userId">
                <option value="">Все сотрудники</option>
                <#list users as user>
                    <option value="${user.id}" <#if selectedUserId?? && selectedUserId == user.id>selected</#if>>${user.name}</option>
                </#list>
            </select>
        </div>
        <div class="col-md-4">
            <label class="form-label">Результат</label>
            <select class="form-select" name="success">
                <option value="" <#if !(success??)>selected</#if>>Все</option>
                <option value="true" <#if success?? && success>selected</#if>>Санкционированный</option>
                <option value="false" <#if success?? && !success>selected</#if>>Несанкционированный</option>
            </select>
        </div>
        <div class="col-md-2">
            <button class="btn btn-dark w-100" type="submit">Применить</button>
        </div>
    </form>

    <div class="table-responsive">
        <table class="table table-bordered align-middle">
            <thead class="table-light">
            <tr>
                <th>Дата</th>
                <th>Время</th>
                <th>Зона</th>
                <th>Сотрудник</th>
                <th>Уровень доступа</th>
                <th>Результат</th>
            </tr>
            </thead>
            <tbody>
            <#list accessAttempts as attempt>
                <tr>
                    <td>${attempt.date}</td>
                    <td>${attempt.time}</td>
                    <td>${attempt.doorName}</td>
                    <td>${attempt.fullName}</td>
                    <td>${attempt.accessLevel?string?replace("LEVEL_", "Уровень ")}</td>
                    <td>
                        <#if attempt.success>
                            <span class="text-success">Доступ получен</span>
                        <#else>
                            <span class="text-danger">Доступ не получен</span>
                        </#if>
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


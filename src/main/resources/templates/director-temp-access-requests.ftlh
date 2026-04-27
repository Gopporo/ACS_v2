<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Временный допуск</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
</head>

<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4">
    <h3 class="text-center mb-4">Запросы на временный допуск</h3>

    <#if errorMessage??>
        <div class="alert alert-danger text-center" role="alert">${errorMessage}</div>
    </#if>
    <#if successMessage??>
        <div class="alert alert-success text-center" role="alert">${successMessage}</div>
    </#if>

    <#if requests?has_content>
        <div class="list-group">
            <#list requests as req>
                <div class="list-group-item">
                    <div class="d-flex w-100 justify-content-between align-items-start gap-3">
                        <div class="flex-grow-1">
                            <div class="d-flex w-100 justify-content-between">
                                <h5 class="mb-1">${req.application.name}</h5>
                                <small class="text-muted">${req.createdAt}</small>
                            </div>
                            <p class="mb-1">
                                Сотрудник: <b>${req.requester.name}</b>,
                                Должность: <b>${req.requester.position!""}</b>,
                                Уровень: <b>${req.requester.userAccessLvl?string?replace("LEVEL_", "Уровень ")}</b>
                            </p>
                            <small class="text-muted">Требуемый допуск: ${req.requestedLevel?string?replace("LEVEL_", "Уровень ")}</small>
                        </div>
                        <div class="d-flex align-items-center">
                            <a class="btn btn-outline-dark" href="/director/temp-access-requests/${req.id}">Просмотр</a>
                        </div>
                    </div>
                </div>
            </#list>
        </div>
    <#else>
        <div class="alert alert-secondary text-center">Пока нет запросов.</div>
    </#if>
</main>

<#include "components/footer.ftl">
</body>
</html>


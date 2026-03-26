<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Редактировать сотрудника (Security)</title>
    <link rel="stylesheet" href="/css/filters.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4" style="max-width: 800px;">
    <h1 class="h3 mb-3">Редактирование сотрудника</h1>
    <div class="mb-3">
        <a class="btn btn-outline-secondary btn-sm" href="/security/workers">Назад к списку</a>
    </div>

    <div class="card mb-4">
        <div class="card-body">
            <form action="/security/workers/update/${worker.id}" method="post">
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
                    <select class="form-select" name="accessLevel" required>
                        <#list accessLevels as lvl>
                            <option value="${lvl}" <#if worker.accessLevel?? && worker.accessLevel == lvl>selected</#if>>${lvl}</option>
                        </#list>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label">Отпечаток (hash)</label>
                    <input type="text" class="form-control" value="${worker.fingerprintHash!''}" disabled>
                </div>

                <button type="submit" class="btn btn-dark">Сохранить</button>
            </form>

            <div class="mt-3 d-flex gap-2 flex-wrap">
                <form action="/security/workers/start-register/${worker.id}" method="post" class="m-0">
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <button type="submit" class="btn btn-primary">Начать регистрацию отпечатка</button>
                </form>

                <a class="btn btn-outline-danger" href="/security/workers/delete/${worker.id}">Удалить (с TCP)</a>
            </div>
        </div>
    </div>
</main>

<#include "components/footer.ftl">
</body>
</html>


<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Запрос на временный допуск</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
</head>

<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4">
    <a href="/director/temp-access-requests" class="btn btn-outline-dark mb-3">Назад</a>

    <h3 class="mb-4">Запрос на временный допуск</h3>

    <div class="row g-3">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Заявка</h5>
                    <p class="mb-1"><b>Название:</b> ${request.application.name}</p>
                    <p class="mb-1"><b>Зона:</b> ${request.application.zone.name}</p>
                    <p class="mb-1"><b>Уровень заявки:</b> ${request.application.accessLevel?string?replace("LEVEL_", "Уровень ")}</p>
                    <p class="mb-1"><b>Описание:</b> ${request.application.disc}</p>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title">Сотрудник</h5>
                    <p class="mb-1"><b>ФИО:</b> ${request.requester.name}</p>
                    <p class="mb-1"><b>Должность:</b> ${request.requester.position!""}</p>
                    <p class="mb-1"><b>Уровень:</b> ${request.requester.userAccessLvl?string?replace("LEVEL_", "Уровень ")}</p>
                    <p class="mb-1"><b>Требуемый временный допуск:</b> ${request.requestedLevel?string?replace("LEVEL_", "Уровень ")}</p>
                </div>
            </div>
        </div>
    </div>

    <div class="d-flex gap-2 mt-4">
        <a href="/director/temp-access-requests/${request.id}/approve" class="btn btn-dark">Одобрить</a>
        <a href="/director/temp-access-requests/${request.id}/deny" class="btn btn-outline-dark">Отклонить</a>
    </div>
</main>

<#include "components/footer.ftl">
</body>
</html>


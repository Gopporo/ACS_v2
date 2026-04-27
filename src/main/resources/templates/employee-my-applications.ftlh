<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="Mark Otto, Jacob Thornton, and Bootstrap contributors">
    <meta name="generator" content="Hugo 0.84.0">
    <title>Course project by Sasha Popov</title>

    <link rel="canonical" href="https://getbootstrap.com/docs/5.0/examples/album/">
    <link rel="stylesheet" href="/css/filters.css">

    <!-- Bootstrap core CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

    <!-- Favicons -->
    <link rel="apple-touch-icon" href="/docs/5.0/assets/img/favicons/apple-touch-icon.png" sizes="180x180">
    <link rel="icon" href="/docs/5.0/assets/img/favicons/favicon-32x32.png" sizes="32x32" type="image/png">
    <link rel="icon" href="/docs/5.0/assets/img/favicons/favicon-16x16.png" sizes="16x16" type="image/png">
    <link rel="manifest" href="/docs/5.0/assets/img/favicons/manifest.json">
    <link rel="mask-icon" href="/docs/5.0/assets/img/favicons/safari-pinned-tab.svg" color="#7952b3">
    <link rel="icon" href="/docs/5.0/assets/img/favicons/favicon.ico">
    <meta name="theme-color" content="#7952b3">

    <style>
        .card {
            height: 300px;
            overflow: hidden; /* Скрыть содержимое за границей карточки */
        }

        .card-body {
            display: flex;
            flex-direction: column; /* Элементы внутри вертикально */
            justify-content: flex-start; /* Кнопки и другие элементы размещены корректно */
        }

        .card-body > * {
            margin-top: 0.2rem; /* Уменьшаем верхние отступы */
            margin-bottom: 0.2rem; /* Уменьшаем нижние отступы */
        }

        .card-text {
            max-height: 100px; /* Фиксированная высота области с текстом */
            overflow-y: auto; /* Вертикальная прокрутка, если текст длинный */
            word-wrap: break-word; /* Перенос длинных слов на новую строку */
        }

        .bd-placeholder-img {
            font-size: 1.125rem;
            text-anchor: middle;
            -webkit-user-select: none;
            -moz-user-select: none;
            user-select: none;
        }

        @media (min-width: 768px) {
            .bd-placeholder-img-lg {
                font-size: 3.5rem;
            }
        }
    </style>
</head>

<body class="h-100">
<!-- Include Navbar -->
<header>
    <#include "components/navbar.ftl">
</header>

<main>
    <#if !(applications??) || !applications?has_content>
        <div class="container py-5">
            <div class="alert alert-secondary text-center mb-0">
                У вас пока нет заявок в работе.
            </div>
        </div>
    <#else>
    <div class="album py-5 bg-light">
        <div class="container">
            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
                <#list applications as apl>
                    <div class="col">
                        <div class="card shadow-sm">
                            <div class="card-body">
                                <h4>${apl.name}</h4>
                                <br>
                                <p class="card-text">Зона доступа: ${apl.zone.name}</p>
                                <br>
                                <p class="card-text">Уровень доступа: ${apl.accessLevel?string?replace("LEVEL_", "Уровень ")}</p>
                                <br>
                                <p class="card-text">Описание: ${apl.disc}</p>
                                <div class="d-flex justify-content-between align-items-center">
                                    <a href="/employee/addReport/${apl.id}" class="btn btn-dark me-2">Написать отчет</a>
                                    <a href="/employee/declineApplication/${apl.id}" class="btn btn-outline-danger me-2">Отклонить заявку</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </div>
    </#if>
</main>

<#include "components/footer.ftl">

</body>
</html>

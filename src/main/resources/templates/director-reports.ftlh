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
            overflow: hidden;
        }

        .card-body {
            display: flex;
            flex-direction: column;
            justify-content: flex-start;
        }

        .card-body > * {
            margin-top: 0.2rem;
            margin-bottom: 0.2rem;
        }

        .card-text {
            max-height: 100px;
            overflow-y: auto;
            word-wrap: break-word;
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

        .error-message {
            text-align: center;
            color: red;
            font-weight: bold;
            margin: 20px 0;
        }
    </style>
</head>

<body class="h-100">
<!-- Include Navbar -->
<header>
    <#include "components/navbar.ftl">
</header>

<main>

    <div class="album py-5 bg-light">
        <div class="container">
            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
                <#list reports as report>
                    <div class="col">
                        <div class="card shadow-sm">
                            <div class="card-body">
                                <h4>Отчет по заявке: ${report.application.name}</h4>
                                <br>
                                <p class="card-text">Сотрудник: ${report.application.user.name}</p>
                                <br>
                                <p class="card-text">Создан: ${report.createdAtFormatted}</p> <!-- Используем форматированную дату -->
                                <br>
                                <div class="d-flex justify-content-between align-items-center">
                                    <a href="/director/report-info/${report.id}" class="btn btn-dark me-2">Подробнее</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </div>
</main>

<#include "components/footer.ftl">

</body>

</html>

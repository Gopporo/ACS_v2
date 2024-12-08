<!doctype html>
<html lang="en">
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
        .divider:after,
        .divider:before {
            content: "";
            flex: 1;
            height: 1px;
            background: #eee;
        }
        .h-custom {
            height: calc(100% - 73px);
        }
        @media (max-width: 450px) {
            .h-custom {
                height: 100%;
            }
        }
    </style>


</head>
<body>

<header>
    <#include "components/navbar.ftl">
</header>

<section class="vh-100">
    <div class="container-fluid h-custom">
        <div class="row d-flex justify-content-center align-items-center h-100">
            <div class="col-md-8 col-lg-6 col-xl-4">
                <form action="/login" method="post">

                    <div class="divider d-flex align-items-center my-4">
                        <p class="text-center fw-bold mx-3 mb-0">Авторизация</p>
                    </div>

                    <!-- Сообщения об ошибке или об успешном логауте-->
                    <div class="container d-flex justify-content-center pt-4">
                        <#if message??>
                            <p>
                                ${message}
                            </p>
                        </#if>
                        <#if errorMessage??>
                            <p>
                                ${errorMessage}
                            </p>
                        </#if>
                    </div>

                    <div class="form-group">
                        <label for="email">Электронная почта</label>
                        <input type="email" class="form-control <#if RequestParameters.error??>is-invalid</#if>" id="email" name="username">
                        <div id="email" class="invalid-feedback">
                            Неверная электронная почта
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="password">Пароль</label>
                        <input type="password" class="form-control <#if RequestParameters.error??>is-invalid</#if>" id="password" name="password">
                        <div id="password" class="invalid-feedback">
                            Неверный пароль
                        </div>
                    </div>

                    <input type="hidden" name="_csrf" value="${_csrf.token}"><br>
                    <!-- Центрируем кнопку "Войти" и делаем её такого же цвета, как фон хедера -->
                    <div class="text-center">
                        <button type="submit" data-mdb-button-init data-mdb-ripple-init class="btn btn-dark btn-lg"
                           style="padding-left: 2.5rem; padding-right: 2.5rem;">Войти</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>





<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>


</body>
</html>

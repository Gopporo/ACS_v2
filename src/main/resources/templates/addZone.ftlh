<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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

</head>

<body >
<header>
    <#include "components/navbar.ftl">
</header>

<section class="py-5 text-center container">
    <div class="row py-lg-5">
        <div class="col-lg-6 col-md-8 mx-auto">
            <h1 class="fw-light">Создание зоны доступа</h1><br>
            <form action="/admin/addZone" method="post">
                <input name="name" type="text" class="form-control" placeholder="Введите название зоны" required><br>
                <input name="disc" type="text" class="form-control" placeholder="Введите описание" required><br>
                <select name="zoneAccessLvl" class="form-select" required>
                    <option value="LEVEL_1">Уровень 1</option>
                    <option value="LEVEL_2">Уровень 2</option>
                    <option value="LEVEL_3">Уровень 3</option>
                    <option value="LEVEL_4">Уровень 4</option>
                    <option value="LEVEL_5">Уровень 5</option>
                </select><br>
                <input type="hidden" name="_csrf" value="${_csrf.token}">
                <button type="submit" class="btn btn-dark">Добавить</button>
            </form>
            <div class="ss">
                <#if errorMessage??>
                    <h2 style="color: black">${errorMessage}</h2>
                </#if>
            </div>
        </div>
    </div>
</section>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>


</body>
</html>
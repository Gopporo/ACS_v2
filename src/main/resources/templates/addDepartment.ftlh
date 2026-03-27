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
            <h1 class="fw-light">Создание нового отдела</h1><br>
            <form action="/admin/addDepartment" method="post">
                <input name="name" type="text" class="form-control" placeholder="Введите название отдела" required><br>
                <div class="mb-3">
                    <label for="departmentHead" class="form-label">Выберите руководителя:</label>
                    <select name="headId" id="departmentHead" class="form-select" required>
                        <#list availableDirectors as director>
                            <option value="${director.id}">${director.name}</option>
                        </#list>
                    </select>
                </div><input type="hidden" name="_csrf" value="${_csrf.token}">
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
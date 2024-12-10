<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="utf-8">
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

<body>
<header>
    <#include "components/navbar.ftl">
</header>

<section class="py-5 text-center container">
    <div class="row py-lg-5">
        <div class="col-lg-6 col-md-8 mx-auto">
            <h1 class="fw-light">Создание аккаунта сотрудника</h1><br>
            <form action="/admin/addUser" method="post">
                <div class="mb-3">
                    <label for="name" class="form-label">ФИО</label>
                    <input type="text" class="form-control" id="name" value="${user.name!''}" readonly>
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Почта</label>
                    <input type="email" class="form-control" id="email" value="${user.email!''}" readonly>
                </div>
                <div class="mb-3">
                    <label for="phone" class="form-label">Номер телефона</label>
                    <input type="tel" class="form-control" id="phone" value="${user.number_phone!''}" readonly>
                </div>
                <input type="hidden" name="userId" value="${user.id!''}">
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="role" id="roleUser" value="ROLE_USER" checked>
                    <label class="form-check-label" for="roleUser">
                        Сотрудник
                    </label>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="radio" name="role" id="roleDirector" value="ROLE_DIRECTOR">
                    <label class="form-check-label" for="roleDirector">
                        Начальник
                    </label>
                </div>
                <br>
                <select id="department" name="department_id" class="form-select">
                    <#list departments as department>
                        <option value="${department.id}">${department.name}</option>
                    </#list>
                </select>
                <br>
                <input name="position" type="text" class="form-control" placeholder="Введите должность" required><br>
                <input name="userAccessLvl" type="number" class="form-control" placeholder="Введите уровень доступа" min="1" max="5" required><br>
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

<script src="/docs/5.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>

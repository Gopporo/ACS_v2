<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Регистрация</title>
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
            <h1 class="fw-light">Регистрация</h1><br>
            <form action="/registration" method="post">
                <input name="name" type="text" class="form-control" placeholder="Введите ФИО" required><br>
                <input type="email" class="form-control" id="email" placeholder="Введите почту" required name="email"><br>
                <input id="password" name="password" type="password" class="form-control" placeholder="Придумайте пароль" required><br>
                <input id="passwordConfirm" name="passwordConfirm" type="password" class="form-control" placeholder="Введите пароль еще раз" required><br>
                <p id="error-message" style="color: red; display: none;">Пароли не совпадают!</p>
                <input type="tel" id="phone" name="number_phone" class="form-control" placeholder="+375(99)999-99-99" pattern="^\+375(\d{2}\)d{3}-\d{2}-\d{2}$" required><br>
                <input type="hidden" name="_csrf" value="${_csrf.token}">
                <button type="submit" class="btn btn-dark">Зарегистрироваться</button>
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
<script>
    const password = document.getElementById('password');
    const passwordConfirm = document.getElementById('passwordConfirm');
    const errorMessage = document.getElementById('error-message');
    const submitButton = document.querySelector('button[type="submit"]');

    function checkPasswords() {
        if (password.value === passwordConfirm.value) {
            errorMessage.style.display = 'none';
            submitButton.disabled = false; // Активируем кнопку
        } else {
            errorMessage.style.display = 'block';
            errorMessage.textContent = 'Пароли не совпадают!';
            submitButton.disabled = true; // Отключаем кнопку
        }
    }

    password.addEventListener('input', checkPasswords);
    passwordConfirm.addEventListener('input', checkPasswords);
</script>

</body>
</html>

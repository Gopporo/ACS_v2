<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <title>Course project by Sasha Popov</title>
</head>
<body>
<header>
    <#include "components/navbar.ftl">
</header>
<main class="d-grid gap-5 py-5 mx-auto" style="width: 600px;">
    <h2 style="text-align: center">Редактирование личных данных</h2>
    <hr>
    <form action="/user/edit/${user.id}" method="post" class="needs-validation" novalidate>
        <div class="mb-3">
            <label for="name" class="form-label">Имя</label>
            <input type="text" class="form-control" id="name" name="name" value="${user.name}" required>
            <div class="invalid-feedback">Пожалуйста, введите имя.</div>
        </div>
        <div class="mb-3">
            <label for="email" class="form-label">Электронная почта</label>
            <input type="email" class="form-control" id="email" name="email" value="${user.email}" required>
            <div class="invalid-feedback">Пожалуйста, введите корректный адрес электронной почты.</div>
        </div>
        <div class="mb-3">
            <label for="password" class="form-label">Пароль</label>
            <input type="password" class="form-control" id="password" name="password" placeholder="Введите новый пароль">
            <div class="form-text">Оставьте пустым, если не хотите менять пароль.</div>
        </div>
        <hr>
        <br>
        <br>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <div class="text-center">
            <button type="submit" class="btn btn-primary">Сохранить</button>
        </div>
    </form>
    <#if errorMessage??>
        <div class="alert alert-danger mt-3" role="alert">${errorMessage}</div>
    </#if>
</main>
<script>
    // Скрипт для валидации формы
    (function () {
        'use strict';
        var forms = document.querySelectorAll('.needs-validation');
        Array.prototype.slice.call(forms).forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>
</body>
</html>

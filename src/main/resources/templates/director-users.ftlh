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
            justify-content: space-between; /* Кнопки и другие элементы размещены корректно */
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

<body  class="h-100">
<!-- Include Navbar -->
<header>
    <#include "components/navbar.ftl">
</header>

<!-- Фильтры
Можно переделать под отделы, но тогда желательно определить отделы
-->
<div class="filters">
    <button class="filter-btn" data-lvl="-1">все</button>
    <button class="filter-btn" data-lvl="LEVEL_1">1</button>
    <button class="filter-btn" data-lvl="LEVEL_2">2</button>
    <button class="filter-btn" data-lvl="LEVEL_3">3</button>
    <button class="filter-btn" data-lvl="LEVEL_4">4</button>
    <button class="filter-btn" data-lvl="LEVEL_5">5</button>
</div>

<!-- Форма для отправки данных -->
<div class="container w-100 d-flex justify-content-end">
    <form class="mb-5" id="filterForm" action="/director/getUsersByAccessLvl" method="get">
        <input type="hidden" name="userAccessLvl" id="hiddenAccessLvl" value="">
    </form>
</div>

<main>

    <!-- Строка для поиска -->
    <div class="container mb-4">
        <form id="searchForm" action="/director/getUsersByName" method="get" class="d-flex justify-content-center">
            <input type="text" class="form-control me-2" name="name" placeholder="Поиск по имени пользователя" aria-label="Поиск" id="searchInput">
            <button class="btn btn-dark" type="submit">Поиск</button>
        </form>
    </div>


    <div class="album py-5 bg-light">
        <div class="container">
            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">
                <#list users as user>
                    <div class="col">
                        <div class="card shadow-sm">
                            <div class="card-body">
                                <h4>${user.name}</h4>
                                <br>
                                <p class="card-text">Занимаемая должность: ${user.position}</p>
                                <br>
                                <p class="card-text">Уровень доступа: ${user.userAccessLvl?string?replace("LEVEL_", "Уровень ")}</p>
                                <br>
                                <p class="card-text">Номер телефона: ${user.number_phone}</p>
                                <div class="d-flex justify-content-between align-items-center">
                                    <div class="btn-group">
                                        <a href="/user/${user.id}" class="btn btn-dark me-2">Просмотр</a>
                                        <#--По идее кнопка редактировать для начальника, чтобы поменять уровень доступа-->
                                        <a href="/director/editUser/${user.id}" class="btn btn-dark">Редактировать</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </div>

</main>


<script>
    // Получаем все кнопки с классом "filter-btn"
    const filterButtons = document.querySelectorAll('.filter-btn');

    // Добавляем обработчик события для каждой кнопки
    filterButtons.forEach(button => {
        button.addEventListener('click', function () {
            // Получаем значение data-lvl кнопки
            const accessLvl = this.getAttribute('data-lvl');

            // Устанавливаем значение в скрытое поле формы
            document.getElementById('hiddenAccessLvl').value = accessLvl;

            // Отправляем форму
            document.getElementById('filterForm').submit();
        });
    });
</script>

<script>
    function validateAndFormatCost() {
        var costInput = document.getElementById('cost');
        var costValue = costInput.value.replace(/,/g, ''); // Remove commas
        if (isNaN(costValue)) {
            alert('Invalid cost value');
            return false;
        }
        costInput.value = parseFloat(costValue).toFixed(2); // Format as a number with two decimal places
        return true;
    }
</script>

<#include "components/footer.ftl">

</body>

</html>
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
<main class="d-grid gap-5 py-5 mx-auto" style="width: 900px;">
    <h2 style="text-align: center">Информация о пользователе</h2>
    <hr>
    <div style="text-align: center">
        <h3>${user.name}</h3>
        <br>
        Должность: <b>${user.position}</b>
        <br>
        Номер телефона: <b>${user.number_phone}</b>
        <br>
        Почта:<b>${user.email}</b>
        <br>
        <form action="/director/updateUser" method="post">
            <input type="hidden" name="userId" value="${user.id}">

            <div class="mb-3">
                <label for="accessLevel" class="form-label">Уровень доступа:</label>
                <select id="accessLevel" name="accessLevel" class="form-select d-inline w-auto" required>
                    <option value="LEVEL_1" <#if user.userAccessLvl?? && user.userAccessLvl == "LEVEL_1">selected</#if>>Уровень 1</option>
                    <option value="LEVEL_2" <#if user.userAccessLvl?? && user.userAccessLvl == "LEVEL_2">selected</#if>>Уровень 2</option>
                    <option value="LEVEL_3" <#if user.userAccessLvl?? && user.userAccessLvl == "LEVEL_3">selected</#if>>Уровень 3</option>
                    <option value="LEVEL_4" <#if user.userAccessLvl?? && user.userAccessLvl == "LEVEL_4">selected</#if>>Уровень 4</option>
                    <option value="LEVEL_5" <#if user.userAccessLvl?? && user.userAccessLvl == "LEVEL_5">selected</#if>>Уровень 5</option>
                </select>
            </div>

            <div class="mb-3">
                <label for="department" class="form-label">Отдел:</label>
                <select id="department" name="departmentId" class="form-select d-inline w-auto">
                    <#list departments as department>
                        <option value="${department.id}" <#if department.id == user.department.id>selected</#if>>
                            ${department.name}
                        </option>
                    </#list>
                </select>
            </div>
            <input type="hidden" name="_csrf" value="${_csrf.token}">

            <button type="submit" class="btn btn-dark mt-2">Сохранить</button>
        </form>

        <br>
    </div>
    <#if errorMessage??>
        <h2 style="color: red">${errorMessage}</h2>
    </#if>
</main>
</body>
</html>
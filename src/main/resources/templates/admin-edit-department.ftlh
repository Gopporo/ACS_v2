<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редактирование отдела</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="h-100">
<header>
    <#include "components/navbar.ftl">
</header>

<main class="container py-4" style="max-width: 700px;">
    <h1 class="h3 mb-3">Редактирование отдела</h1>
    <form action="/admin/updateDepartment/${department.id}" method="post">
        <div class="mb-3">
            <label class="form-label">Название отдела</label>
            <input name="name" type="text" class="form-control" value="${department.name!''}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Глава отдела</label>
            <select name="headId" class="form-select">
                <option value="">Не назначен</option>
                <#list availableDirectors as director>
                    <option value="${director.id}" <#if department.head?? && director.id == department.head.id>selected</#if>>${director.name}</option>
                </#list>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">Сотрудники отдела</label>
            <div class="border rounded p-2" style="max-height: 240px; overflow-y: auto;">
                <#list availableEmployees as employee>
                    <div class="form-check">
                        <input class="form-check-input"
                               type="checkbox"
                               name="employeeIds"
                               value="${employee.id}"
                               id="employee-${employee.id}"
                               <#if selectedEmployeeIds?? && selectedEmployeeIds?seq_contains(employee.id)>checked</#if>>
                        <label class="form-check-label" for="employee-${employee.id}">
                            ${employee.name} (${employee.position!'-'})
                        </label>
                    </div>
                </#list>
            </div>
        </div>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <button type="submit" class="btn btn-dark">Сохранить</button>
        <a href="/admin/departments" class="btn btn-outline-secondary ms-2">Отмена</a>
    </form>
</main>

<#include "components/footer.ftl">
</body>
</html>

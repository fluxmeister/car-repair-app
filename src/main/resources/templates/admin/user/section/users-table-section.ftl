<#if userList??>
    <div class="table-responsive">
        <table id="resultsTable" class="table table-hover">
            <thead>
            <tr>
                <th>User ID</th>
                <th>AFM</th>
                <th>Email</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Type</th>
                <th>Street</th>
                <th>Number</th>
                <th>Zip Code</th>
                <th>Edit</th>
                <th>Delete</th>
                <th>Vehicles</th>
            </tr>
            </thead>
            <tbody>
            <#list userList as user>
                <tr>
                    <td>${user.userID}</td>
                    <td>${user.afm}</td>
                    <td>${user.email}</td>
                    <td>${user.firstName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.type}</td>
                    <td>${user.addressStreet}</td>
                    <td>${user.addressNumber}</td>
                    <td>${user.addressZipCode}</td>
                    <td>
                        <form action="/admin/users/${user.userID}/edit" method="GET">
                            <button type="submit" class="btn btn-success">
                                <span class="glyphicon glyphicon-cog"></span>
                            </button>
                        </form>
                    </td>
                    <td>
                        <form action="/admin/users/${user.userID}/delete" method="POST">
                            <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure?')">
                                <span class="glyphicon glyphicon-remove"></span>
                            </button>
                        </form>
                    </td>
                    <td>
                        <form action="/admin/users/${user.userID}/vehicles" method="GET">
                            <button type="submit" class="btn btn-info">
                                <span class="glyphicon glyphicon-dashboard"></span>
                            </button>
                        </form>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</#if>

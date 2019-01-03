<html>
    <body>
        <form action="/auth-server/oauth/authorize" method="POST">
            <label for="client_id">client_id</label>
            <input type="text" name="client_id" value="nimbus1">
            <label for="client_secret">client_secret</label>
            <input type="text" name="client_secret" value="nimbus1">
            <label for="response_type">response_type</label>
            <input type="text" name="response_type" value="code" />
            <label for="redirect_uri">redirect_uri</label>
            <input type="text" name="redirect_uri" value="http://www.google.com" />
            <label for="scope">scope</label>
            <input type="text" name="scope" value="read" />
            <input type="submit" value="submit">
        </form>
    </body>
</html>
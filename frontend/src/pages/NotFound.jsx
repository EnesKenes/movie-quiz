import {Link, useLocation} from "react-router-dom";
import {useEffect} from "react";
import {Button} from "@/components/ui/button";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Film, Home} from "lucide-react";

const NotFound = () => {
  const location = useLocation();

  useEffect(() => {
    console.error(
      "404 Error: User attempted to access non-existent route:",
      location.pathname
    );
  }, [location.pathname]);

  return (
    <div className="quiz-container">
      <div className="max-w-md w-full animate-fade-in">
        <Card className="glass-card cinema-glow">
          <CardHeader className="text-center space-y-4">
            <div className="flex justify-center">
              <Film className="h-16 w-16 text-muted-foreground animate-bounce-in"/>
            </div>
            <CardTitle className="text-4xl font-bold">404</CardTitle>
            <p className="text-xl text-muted-foreground">
              Oops! This page doesn't exist
            </p>
            <p className="text-sm text-muted-foreground">
              Looks like this scene was cut from the final version.
            </p>
          </CardHeader>

          <CardContent>
            <Button asChild className="w-full h-12 text-lg" size="lg">
              <Link to="/">
                <Home className="mr-2 h-5 w-5"/>
                Return to Home
              </Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default NotFound;
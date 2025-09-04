import { Link, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Home } from "lucide-react";

const NotFound = () => {
  const location = useLocation();
  const [catUrl, setCatUrl] = useState(null);
  const [imgLoaded, setImgLoaded] = useState(false);

  useEffect(() => {
    console.error(
      "404 Error: User attempted to access non-existent route:",
      location.pathname
    );

    setImgLoaded(false); // reset loading state on URL change
    fetch("https://api.thecatapi.com/v1/images/search")
      .then((res) => res.json())
      .then((data) => setCatUrl(data?.[0]?.url || null))
      .catch(() => setCatUrl(null));
  }, [location.pathname]);

  return (
    <div className="quiz-container">
      <div className="max-w-md w-full animate-fade-in">
        <Card className="glass-card cinema-glow">
          <CardHeader className="text-center space-y-4">
            <CardTitle className="text-4xl font-bold">404</CardTitle>
            <p className="text-xl text-muted-foreground">
              Oops! This page doesn't exist
            </p>
          </CardHeader>

          <CardContent className="flex flex-col items-center space-y-4">
            {catUrl && (
              <div className="flex flex-col items-center">
                <img
                  src={catUrl}
                  alt="Random cat for 404 page"
                  className={`mx-auto rounded-lg shadow-md max-w-md max-h-80 w-full h-auto object-contain transition-opacity duration-2000 ${
                    imgLoaded ? "opacity-100" : "opacity-0"
                  }`}
                  loading="lazy"
                  onLoad={() => setImgLoaded(true)}
                  onError={() => setImgLoaded(false)}
                />
                <p
                  className={`text-sm text-muted-foreground mt-2 transition-opacity duration-2000 ${
                    imgLoaded ? "opacity-100" : "opacity-0"
                  }`}
                >
                  While you're here, enjoy this random cat!
                </p>
              </div>
            )}

            <Button asChild className="w-full h-12 text-lg" size="lg">
              <Link to="/">
                <Home className="mr-2 h-5 w-5" />
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

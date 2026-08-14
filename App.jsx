import	{	BrowserRouter,	Routes,	Route,	Link	}	from	"react-router-dom";
 function	Home()	{	return	<h2>Home	Page</h2>;	}
 function	About()	{	return	<h2>About	Page</h2>;	} 
function	Contact()	{	return	<h2>Contact	Page</h2>;	} 

function	App()	{
 return	( 
<BrowserRouter>
 <nav> 
<Link	to="/">Home</Link>	|	<Link	to="/about">About</Link>	
|	<Link	to="/contact">Contact</Link> </nav> 
<Routes> 
<Route	path="/"	element={<Home	/>}	/> 
<Route	path="/about"	element={<About	/>}	/> 
<Route	path="/contact"	element={<Contact	/>}	/>
 </Routes> </BrowserRouter> );
 }
export	default	App;
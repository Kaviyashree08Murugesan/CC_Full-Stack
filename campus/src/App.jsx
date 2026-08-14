import {
  BrowserRouter,
  Routes,
  Route,
  Link,
  useParams
} from "react-router-dom";


// Student Data
const studentData = [
  {
    id: 1,
    name: "Ananya",
    course: "Full Stack"
  },
  {
    id: 2,
    name: "Rahul",
    course: "Data Science"
  },
  {
    id: 3,
    name: "Priya",
    course: "Full Stack"
  }
];


// Home Component
function Home() {
  return (
    <div className="home">

      <h1>Welcome to Campus Connection</h1>

      <p>
        Student Portal
      </p>

      <Link to="/students" className="button">
        View Students
      </Link>

    </div>
  );
}


// Students Component
function Students() {
  return (
    <div>

      <h2>Student List</h2>

      <div className="student-list">

        {studentData.map((student) => (
          <div className="student-card" key={student.id}>

            <h3>{student.name}</h3>

            <p>
              Course: {student.course}
            </p>

            <Link
              to={`/students/${student.id}`}
              className="view-button"
            >
              View Details
            </Link>

          </div>
        ))}

      </div>

    </div>
  );
}


// Student Detail Component
function StudentDetail() {

  const { id } = useParams();

  const student = studentData.find(
    (student) => student.id === parseInt(id)
  );


  if (!student) {
    return (
      <div>
        <h2>Student Not Found</h2>

        <Link to="/students">
          ← Back to Students
        </Link>
      </div>
    );
  }


  return (
    <div className="student-detail">

      <h2>{student.name}</h2>

      <div className="detail-box">

        <p>
          <strong>Student ID:</strong> {student.id}
        </p>

        <p>
          <strong>Name:</strong> {student.name}
        </p>

        <p>
          <strong>Course:</strong> {student.course}
        </p>

      </div>

      <Link to="/students" className="back-button">
        ← Back to List
      </Link>

    </div>
  );
}


// Navbar Component
function Navbar() {
  return (
    <nav className="navbar">

      <div className="logo">
        Campus Connection
      </div>

      <div className="nav-links">

        <Link to="/">
          Home
        </Link>

        <Link to="/students">
          Students
        </Link>

      </div>

    </nav>
  );
}


// Main App
function App() {
  return (
    <BrowserRouter>

      <Navbar />

      <main className="container">

        <Routes>

          <Route
            path="/"
            element={<Home />}
          />

          <Route
            path="/students"
            element={<Students />}
          />

          <Route
            path="/students/:id"
            element={<StudentDetail />}
          />

        </Routes>

      </main>

    </BrowserRouter>
  );
}


export default App;